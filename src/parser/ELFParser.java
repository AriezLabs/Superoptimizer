package parser;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class ELFParser {
    private static final int SIZEOFUINT64STAR = 8;
    // page-aligned ELF header for storing file header (64 bytes),
    // program header (56 bytes), and code length (8 bytes)
    private static final int ELF_HEADER_LEN = 4096;

    public static ParsedELFBinary parse(String path) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(path, "r")) {
            FileChannel inChannel = file.getChannel();
            long fileSize = inChannel.size();
            ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            inChannel.read(buffer);
            buffer.rewind();

            byte[] magic = new byte[4];
            buffer.get(magic, 0, 4);
            if (magic[0] != 0x7f || magic[1] != 'E' || magic[2] != 'L' || magic[3] != 'F') {
                throw new RuntimeException("File does not have ELF magic");
            }
            long entry_point = buffer.getLong(10 * SIZEOFUINT64STAR);
            long binary_length = buffer.getLong(12 * SIZEOFUINT64STAR);
            long code_length = buffer.getLong(15 * SIZEOFUINT64STAR);

            // TODO: validate rest of header

            buffer.position(ELF_HEADER_LEN);
            byte[] code = new byte[(int) code_length];
            buffer.get(code, 0, code.length);
            byte[] data = new byte[(int) (binary_length - code_length)];
            buffer.get(data, 0, data.length);

            if (buffer.hasRemaining()) {
                throw new RuntimeException("Did not reach EOF after reading code and data segment. Corrupted file?");
            }
            return new ParsedELFBinary(code, data, entry_point);
        }
    }
}
