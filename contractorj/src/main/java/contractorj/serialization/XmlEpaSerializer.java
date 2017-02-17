package contractorj.serialization;

import contractorj.model.Action;
import contractorj.model.Epa;
import contractorj.model.State;
import contractorj.model.Transition;
import java.io.ByteArrayOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlEpaSerializer implements EpaSerializer {

  @Override
  public String serialize(final Epa epa) {

    final Document doc = getDocument();
    final Element abstraction = getAbstractionTag(epa, doc);

    doc.appendChild(abstraction);

    for (final Action action : epa.getActions()) {

      final Element label = doc.createElement("label");
      label.setAttribute("name", action.toString());

      abstraction.appendChild(label);
    }

    for (final State state : epa.getStates()) {

      final Element stateElement = getStateElement(epa, state, doc);

      abstraction.appendChild(stateElement);
    }

    return documentToString(doc);
  }

  private Document getDocument() {

    final DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();

    final DocumentBuilder icBuilder;
    try {
      icBuilder = icFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }

    return icBuilder.newDocument();
  }

  private String documentToString(final Document doc) {

    final Transformer transformer;
    try {
      transformer = TransformerFactory.newInstance().newTransformer();
    } catch (TransformerConfigurationException e) {
      throw new RuntimeException(e);
    }

    transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

    final DOMSource source = new DOMSource(doc);
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final StreamResult streamResult = new StreamResult(output);

    try {
      transformer.transform(source, streamResult);
    } catch (TransformerException e) {
      throw new RuntimeException();
    }

    return output.toString();
  }

  private Element getStateElement(final Epa epa, final State state, final Document doc) {

    final Element stateElement = doc.createElement("state");

    stateElement.setAttribute("name", getStateName(state));

    for (final Action enabledAction : state.getEnabledActions()) {
      final Element enabledLabel = doc.createElement("enabled_label");
      enabledLabel.setAttribute("name", enabledAction.toString());

      stateElement.appendChild(enabledLabel);
    }

    for (final Transition transition : epa.getTransitionsWithSource(state)) {

      final String destinationName = getStateName(transition.getTarget());
      final boolean isDestinationError = destinationName.equals("ERROR");

      final Element transitionElement = doc.createElement("transition");
      transitionElement.setAttribute("destination", destinationName);
      transitionElement.setAttribute("label", transition.getAction().toString());
      transitionElement.setAttribute("uncertain", String.valueOf(transition.isUncertain()));
      transitionElement.setAttribute("exitCode", transition.isThrowing() ? "Exception" : "Ok");
      transitionElement.setAttribute("violates_invariant", String.valueOf(isDestinationError));

      stateElement.appendChild(transitionElement);
    }

    return stateElement;
  }

  private Element getAbstractionTag(final Epa epa, final Document doc) {

    final Element abstraction = doc.createElement("abstraction");

    abstraction.setAttribute("initial_state", getStateName(epa.getInitialState()));
    abstraction.setAttribute("input_format", "code-with-pre-methods");
    abstraction.setAttribute("name", epa.getClassName());

    return abstraction;
  }

  private String getStateName(State state) {

    if (state.equals(State.ERROR)) {
      return "ERROR";
    }

    return state
        .getEnabledActions()
        .stream()
        .map(action -> action.getMethod().getJavaNameWithArgumentTypes())
        .reduce((s1, s2) -> s1 + "$" + s2)
        .orElse("");
  }
}
