package examples.marker_states;

import java.nio.*;

/**
 * Created by Usuario on 04/07/2017.
 */

// TODOS LOS METODOS ESTAN IMPLEMENTADOSS COMO DUMMIES PORQUE SOLO SE CREA EL OBJETO

public class DirectByteBuffer extends ByteBuffer{
    DirectByteBuffer(int cap) {
        super(-1, 0, cap, cap);
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public ByteBuffer slice() {
        return null;
    }

    @Override
    public ByteBuffer duplicate() {
        return null;
    }

    @Override
    public ByteBuffer asReadOnlyBuffer() {
        return null;
    }

    @Override
    public byte get() {
        return 0;
    }

    @Override
    public ByteBuffer put(byte b) {
        return null;
    }

    @Override
    public byte get(int index) {
        return 0;
    }

    @Override
    public ByteBuffer put(int index, byte b) {
        return null;
    }

    @Override
    public ByteBuffer compact() {
        return null;
    }

    @Override
    public boolean isDirect() {
        return false;
    }

    @Override
    byte _get(int i) {
        return 0;
    }

    @Override
    void _put(int i, byte b) {

    }

    @Override
    public char getChar() {
        return 0;
    }

    @Override
    public ByteBuffer putChar(char value) {
        return null;
    }

    @Override
    public char getChar(int index) {
        return 0;
    }

    @Override
    public ByteBuffer putChar(int index, char value) {
        return null;
    }

    @Override
    public CharBuffer asCharBuffer() {
        return null;
    }

    @Override
    public short getShort() {
        return 0;
    }

    @Override
    public ByteBuffer putShort(short value) {
        return null;
    }

    @Override
    public short getShort(int index) {
        return 0;
    }

    @Override
    public ByteBuffer putShort(int index, short value) {
        return null;
    }

    @Override
    public ShortBuffer asShortBuffer() {
        return null;
    }

    @Override
    public int getInt() {
        return 0;
    }

    @Override
    public ByteBuffer putInt(int value) {
        return null;
    }

    @Override
    public int getInt(int index) {
        return 0;
    }

    @Override
    public ByteBuffer putInt(int index, int value) {
        return null;
    }

    @Override
    public IntBuffer asIntBuffer() {
        return null;
    }

    @Override
    public long getLong() {
        return 0;
    }

    @Override
    public ByteBuffer putLong(long value) {
        return null;
    }

    @Override
    public long getLong(int index) {
        return 0;
    }

    @Override
    public ByteBuffer putLong(int index, long value) {
        return null;
    }

    @Override
    public LongBuffer asLongBuffer() {
        return null;
    }

    @Override
    public float getFloat() {
        return 0;
    }

    @Override
    public ByteBuffer putFloat(float value) {
        return null;
    }

    @Override
    public float getFloat(int index) {
        return 0;
    }

    @Override
    public ByteBuffer putFloat(int index, float value) {
        return null;
    }

    @Override
    public FloatBuffer asFloatBuffer() {
        return null;
    }

    @Override
    public double getDouble() {
        return 0;
    }

    @Override
    public ByteBuffer putDouble(double value) {
        return null;
    }

    @Override
    public double getDouble(int index) {
        return 0;
    }

    @Override
    public ByteBuffer putDouble(int index, double value) {
        return null;
    }

    @Override
    public DoubleBuffer asDoubleBuffer() {
        return null;
    }
}
