package examples.marker_states;

import contractor.java.nio.Bits;
import contractor.java.nio.ByteBuffer;
import contractor.java.nio.HeapByteBuffer;
import examples.ContractorIgnored;

/*
    Esta clase la construí del siguiente modo.

    La clase ByteBuffer es abstracta, entonces es complicado poder hacer una EPA.
    ByteBuffer usa dos implementaciones concretas, una es HeapByteBuffer. Esa clase es privada del package.

    Con soot modifiqué el .class del HeapByteBuffer para hacerlo público y todas sus cosas.
        Esto generó conflictos de access level de sus clases padres. Por eso modifique a sus padres.

    Con soot modifiqué también Buffer, ByteBuffer para que sean totalmente publicas.

    Luego agarré esas clases y genere un jar con la siguiente estructura:
        java/nio/*.class donde * son las clases de arriba.

    A ese jar le modifiqué el namespace de todas sus clases. De ese modo evito una colisión con la JDK.
    Es decir si dicen package java.nio; ahora dice package contractor.java.nio;
             si dicen import java.nio.******; ahora dicen import contractor.java.nio.******


    Para obtener las clases compiladas uso runtime de java 7 que ya tiene contractor (es un .jar).
    Para modificar los access modifiers uso soot.
    Para generar el jar hago: jar cvf output.jar *
    Para hacer el cambio de namespace uso el "jar jar" de google https://stackoverflow.com/questions/12612726/using-jarjar-repackaging-tool

    Importante: mover el contenido del nio_reduced_repackage.jar al classpath de contractor (para generar la epa).
 */

public class WrapperHeapByteBuffer extends HeapByteBuffer {
    public static boolean WrapperHeapByteBuffer_pre(int i, int i1) {return i == i1 && i >= 0 && Bits.byteOrder != null;}
    public WrapperHeapByteBuffer(int i, int i1) {
        super(i, i1);
        // by default java value is false
        isReadOnly = false;
    }

    @ContractorIgnored
    public WrapperHeapByteBuffer(byte[] bytes, int i, int i1) {
        super(bytes, i, i1);
    }

    // lo necesitaba
    @ContractorIgnored
    public WrapperHeapByteBuffer(byte[] bytes, int i, int i1, int i2, int i3, int i4) {
        super(bytes, i, i1, i2, i3, i4);
    }

    public boolean put_pre(){
        if (position() >= limit())
            return false;

        return true;
    }
    public ByteBuffer put(byte x) {
        return super.put(x);
    }

    public boolean put_pre(int i, byte x){
        if ((i < 0) || (i >= limit)) // TODO: No entiendo como es valido por mezclar estado y argumentos
            return false;

        return true;
    }
    public ByteBuffer put(int i, byte x) {
        return super.put(i, x);
    }

    public boolean put_pre(byte[] src, int offset, int length){
        return offset >= 0 && length >= 0 && src != null && src.length - (offset + length) >= 0 && length <= remaining();  // TODO: No entiendo como es valido por mezclar estado y argumentos
    }

    public ByteBuffer put(byte[] src, int offset, int length) {
        return super.put(src,offset,length);
    }

    static boolean checkBoundsBoolean(int off, int len, int size) { // package-private
        if ((off | len | (off + len) | (size - (off + len))) < 0)
            return true;
        return false;
    }

    public boolean put_pre(ByteBuffer src){
        if (src == null)
            return false;

        boolean bufferInv = src.markValue() >= -1  && src.offset >= 0 && src.position() >= 0 && src.markValue() <= src.position()  && src.position() <= src.limit() && src.limit() <= src.capacity();//(undefinedMark_inv || definedMark_inv) && markValue() >= -1;
        boolean heapByteBuffer_inv = src.hb != null && src.hb.length >= 0 && src.hb.length == src.capacity(); // HeapByteBuffer inv - see ByteBuffer source code (there is a comment about this)

        if ( !(bufferInv && heapByteBuffer_inv))
            return false;

        if (!src.isDirect())
             return false;

        if (src == this) // esto es turbio, deberia andar igual si lo saco.
            return false;

        // TODO: ESTO PONIENDOLO DENTRO DEL IF DEL ISDIRECT DEBERIA ALCANZAR
        // EL TEMA ES QUE DE COMO ESTA CODIFICADO EL ISDIRECT GENERA QUE EN LA LLAMADA AL PRE DEVUELVE FALSE
        // Y LUEGO EN EL METODO DEVUELVE TRUE
        // EL VALOR DEBE SER FIJADO UNA VEZ QUE DEVOLVIO UN VALOR PARA UNA REFERENCIA
        // LA COMPARACION DEL src==this debe pasar por el mismo problema
        if (checkBoundsBoolean(ix(position()), src.remaining(), hb.length))
            return false;


        if (src instanceof HeapByteBuffer){
            if (src == this)
                return false;

            HeapByteBuffer sb = (HeapByteBuffer)src;
            int n = sb.remaining();
            if (n > remaining())
                return false;
        } else if (src.isDirect()){
            int n = src.remaining();
            if (n > remaining())
                return false;

            if (checkBoundsBoolean(ix(position()), n, hb.length)) // No funciona no se porque.
                return false;

        } else{
            if (src == this)
                return false;
            if (isReadOnly())
                return false;
            int n = src.remaining();
            if (n > remaining())
                return false;


        }

        return true;
    }
    public ByteBuffer put(ByteBuffer src) {
        return super.put(src);
    }

    public boolean get_pre(){
        if (position >= limit)
            return false;
        return true;
    }

    public byte get() {
        return super.get();
    }

    public boolean get_pre(int i){
        if ((i < 0) || (i >= limit))
            return false;
        return true;
    }
    public byte get(int i) {
        return super.get(i);
    }

    public boolean get_pre(byte[] dst, int offset, int length){
        if (checkBoundsBoolean(offset, length, dst.length))
            return false;

        if (length > remaining())
            return false;

        return true;
    }

    public ByteBuffer get(byte[] dst, int offset, int length) {
        return super.get(dst, offset, length);
    }

    public ByteBuffer compact(){
        return super.compact();
    }

    public final boolean wrapper_hasArray() {
        return (hb != null) && !isReadOnly;
    }

    public boolean wrapper_array_pre(){
        return wrapper_hasArray();
    }
    public byte[] wrapper_array(){
        return super.array();
    }

    public boolean inv() {

        // buffer docs states this invariant
        boolean bufferInv = markValue() >= -1  && this.offset >= 0 && position() >= 0 && markValue() <= position()  && position() <= limit() && limit() <= capacity();//(undefinedMark_inv || definedMark_inv) && markValue() >= -1;
        boolean heapByteBuffer_inv = this.hb != null && this.hb.length >= 0 && this.hb.length == capacity() && isReadOnly == false; // HeapByteBuffer inv - see ByteBuffer source code (there is a comment about this)

        return bufferInv
                && heapByteBuffer_inv
                && Bits.byteOrder != null; // workaround
    }
}
