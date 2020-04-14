package parser;

public class ParsedELFBinary {
    public byte[] codeSegment;
    public byte[] dataSegment;
    public long entryPoint;

    public ParsedELFBinary(byte[] codeSegment, byte[] dataSegment, long entryPoint) {
        this.codeSegment = codeSegment;
        this.dataSegment = dataSegment;
        this.entryPoint = entryPoint;
    }
}
