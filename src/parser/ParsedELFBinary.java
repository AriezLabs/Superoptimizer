package parser;

public class ParsedELFBinary {
    public Instruction[] instructions;
    public byte[] codeSegment;
    public byte[] dataSegment;
    public long entryPoint;

    public ParsedELFBinary(byte[] codeSegment, byte[] dataSegment, long entryPoint) {
        this.codeSegment = codeSegment;
        this.dataSegment = dataSegment;
        this.entryPoint = entryPoint;

        instructions = new Instruction[codeSegment.length / 4];
        for (int i = 0; i < codeSegment.length; i += 4) {

        }
    }
}
