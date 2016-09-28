package j2bpl.translation;

import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.jimple.JimpleBody;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public abstract class Method {

    private static final HashMap<String, LocalMethod> localMethodsFactoryCache = new HashMap<>();

    private Class theClass;

    protected final SootMethod sootMethod;

    public static Method create(Class theClass, SootMethod sootMethod) {

        if (!theClass.isApplicationClass()) {
            return new ExternalMethod(theClass, sootMethod);
        }

        final String translatedMethodName = getTranslatedMethodName(theClass, sootMethod);

        if (!localMethodsFactoryCache.containsKey(translatedMethodName)) {

            final JimpleBody jimpleBody = (JimpleBody) sootMethod.getActiveBody();
            final LocalMethod localMethod = new LocalMethod(theClass, jimpleBody);

            localMethodsFactoryCache.put(translatedMethodName, localMethod);
        }

        return localMethodsFactoryCache.get(translatedMethodName);
    }

    /**
     * Returns the translated name of the method.
     * <p>
     * <p>The name is the method's fully qualified name with boogie-identifier illegal characters escaped, and mangled
     * with its parameter types.
     */
    private static String getTranslatedMethodName(Class theClass, SootMethod sootMethod) {

        final String baseName = theClass.getTranslatedName() +
                (sootMethod.isStatic() ? "." : "#") +
                StringUtils.scapeIllegalIdentifierCharacters(sootMethod.getName());

        return mangleMethodName(sootMethod, baseName);
    }

    /**
     * Mangles the name of a method.
     */
    private static String mangleMethodName(SootMethod sootMethod, String nonMangledName) {

        final StringBuilder stringBuilder = new StringBuilder(nonMangledName);

        @SuppressWarnings("unchecked")
        final List<Type> parameterTypes = sootMethod.getParameterTypes();

        for (final Type parameterType : parameterTypes) {

            final String paramTypeName;

            if (parameterType instanceof RefType) {

                final RefType refType = (RefType) parameterType;
                paramTypeName = getQualifiedClassName(refType.getSootClass());

            } else {
                paramTypeName = TypeTranslator.translate(parameterType);
            }

            stringBuilder.append("$")
                    .append(StringUtils.scapeIllegalIdentifierCharacters(paramTypeName));

        }

        return stringBuilder.toString();
    }

    /**
     * Returns the java qualified name of the class.
     */
    private static String getQualifiedClassName(SootClass sootClass) {

        final String packageName = sootClass.getJavaPackageName();

        return sootClass.getJavaPackageName() +
                (packageName.isEmpty() ? "" : ".") +
                sootClass.getJavaStyleName();
    }

    public abstract String getTranslatedProcedure();

    public abstract boolean isExternalMethod();

    public abstract boolean isClassInitializer();

    protected Method(Class theClass, SootMethod sootMethod) {
        this.theClass = theClass;
        this.sootMethod = sootMethod;
    }

    public String getTranslatedName() {
        return getTranslatedMethodName(theClass, sootMethod);
    }

    public boolean isStatic() {
        return sootMethod.isStatic();
    }

    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }

        if (!(other instanceof Method)) {
            return false;
        }

        final Method method = (Method) other;

        return Objects.equals(getTranslatedName(), method.getTranslatedName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTranslatedName());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<" + getTranslatedName() + ">";
    }
}
