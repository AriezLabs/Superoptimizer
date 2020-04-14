package parser;

/**
 * Represents instruction types such as LUI, JAL, ...
 */
public class InstructionType {
    Mnemonic mnemonic;
    Format format;
    byte opcode;
    Byte f3;
    Byte f7;

    public InstructionType(Mnemonic mnemonic, Format format, byte opcode, Byte f3, Byte f7) {
        this.mnemonic = mnemonic;
        this.format = format;
        this.opcode = opcode;
        this.f3 = f3;
        this.f7 = f7;
    }

    // to avoid having to cast integer literals to Byte everytime
    public InstructionType(Mnemonic mnemonic, Format format, int opcode, Integer f3, Integer f7) {
        this.mnemonic = mnemonic;
        this.format = format;
        this.opcode = (byte) opcode;
        if (f3 != null) {
            this.f3 = f3.byteValue();
        }
        if (f7 != null) {
            this.f7 = f7.byteValue();
        }
    }

    int extractBits(int data, int from, int to) {
        return (data >>> from) & (~0 >>> (31 - (to - from)));
    }
    // Check if 32-bit word is this type of instruction
    // f3/f7 are null iff this type of instruction does not have a f3/f7 code
    boolean matches(int binary) {
        return (extractBits(binary, 0, 6)) == opcode
                && (f3 == null || extractBits(binary, 12, 14) == f3)
                && (f7 == null || extractBits(binary, 25, 31) == f7);
    }

}
