package parser;

/**
 * Represents instruction types such as LUI, JAL, ...
 */
public class InstructionType {
    String mnemonic;
    String type;
    Byte opcode = null;
    Byte f3 = null;
    Byte f7 = null;

    public InstructionType(String mnemonic, String type, Byte opcode, Byte f3, Byte f7) {
        this.mnemonic = mnemonic;
        this.type = type;
        this.opcode = opcode;
        this.f3 = f3;
        this.f7 = f7;
    }

    // to avoid having to cast integer literals to Byte everytime
    public InstructionType(String mnemonic, String type, Integer opcode, Integer f3, Integer f7) {
        this.mnemonic = mnemonic;
        this.type = type;
        this.opcode = opcode.byteValue();
        this.f3 = f3.byteValue();
        this.f7 = f7.byteValue();
    }

    // Check if 32-bit word is this type of instruction
    // f3/f7 are null iff this type of instruction does not have a f3/f7 code
    public boolean matches(int binary) {
        return (binary & 0b111111) == opcode
                && (f3 == null || ((binary >> 11) & 0b111) == f3)
                && (f7 == null || ((binary >> 24) & 0b1111111) == f7);
    }

}
