package parser;

public class ParsedELFBinary {
    public Instruction[] instructions;
    public byte[] codeSegment;
    public byte[] dataSegment;
    public long entryPoint;

    public ParsedELFBinary(byte[] codeSegment, byte[] dataSegment, Instruction[] instructions, long entryPoint) {
        this.codeSegment = codeSegment;
        this.dataSegment = dataSegment;
        this.instructions = instructions;
        this.entryPoint = entryPoint;
    }
}
