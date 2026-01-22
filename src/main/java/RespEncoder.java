import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RespEncoder {

    private static final String CRLF = "\r\n";
    private static final byte[] CRLF_BYTES = CRLF.getBytes(StandardCharsets.UTF_8);

    // RESP type prefixes
    private static final char SIMPLE_STRING = '+';
    private static final char ERROR = '-';
    private static final char INTEGER = ':';
    private static final char BULK_STRING = '$';
    private static final char ARRAY = '*';

    private final OutputStream out;

    public RespEncoder(OutputStream out) {
        this.out = out;
    }


    public RespEncoder writeSimpleString(String str) throws IOException {
        out.write(SIMPLE_STRING);
        out.write(str.getBytes(StandardCharsets.UTF_8));
        out.write(CRLF_BYTES);
        return this;
    }
    public RespEncoder WriteBulkString(String str) throws IOException {
        if(str == null) {
            return writeNullBulkString();
        }
        out.write(BULK_STRING);
        out.write(String.valueOf(str.length()).getBytes(StandardCharsets.UTF_8));
        out.write(CRLF_BYTES);
        out.write(str.getBytes(StandardCharsets.UTF_8));
        out.write(CRLF_BYTES);
        return this;
    }
    public RespEncoder WriteInteger(long i) throws IOException {
        out.write(INTEGER);
        out.write(String.valueOf(i).getBytes(StandardCharsets.UTF_8));
        out.write(CRLF_BYTES);
        return this;
    }
    // Write an array header (e.g., "*3\r\n")
    public RespEncoder writeArrayHeader(int arraySize) throws IOException {
        out.write(ARRAY);
        out.write(String.valueOf(arraySize).getBytes(StandardCharsets.UTF_8));
        out.write(CRLF_BYTES);
        return this;
    }
    public RespEncoder writeError(String error) throws IOException {
        out.write(ERROR);
        out.write(error.getBytes(StandardCharsets.UTF_8));
        out.write(CRLF_BYTES);
        return this;
    }
    public RespEncoder writeNullBulkString() throws IOException {
        out.write(BULK_STRING);
        out.write("-1".getBytes(StandardCharsets.UTF_8));
        out.write(CRLF_BYTES);
        return this;
    }
    //Write a null array (e.g., "*-1\r\n")
    public RespEncoder WriteNullArray() throws IOException {
        out.write(ARRAY);
        out.write("-1".getBytes(StandardCharsets.UTF_8));
        out.write(CRLF_BYTES);
        return this;
    }
    public RespEncoder flush() throws IOException {
        out.flush();
        return this;
    }
    public RespEncoder WriteBulkArray(String...values) throws IOException {
        if(values == null) {
            return writeNullBulkString();
        }
        writeArrayHeader(values.length);
        for(String value : values) {
            WriteBulkString(value);
        }
        return this;
    }
    public RespEncoder WriteBulkArray(List<CachKey> values) throws IOException {
        if(values == null) {
            return writeNullBulkString();
        }
        writeArrayHeader(values.size());
        for(CachKey value : values) {
            WriteBulkString(value.value);
        }
        return this;
    }
    public RespEncoder ok() throws IOException {
        return writeSimpleString("OK").flush();
    }

    public RespEncoder pong() throws IOException {
        return writeSimpleString("PONG").flush();
    }

    public RespEncoder errUnknownCommand() throws IOException {
        return writeError("ERR unknown command").flush();
    }

    public RespEncoder errWrongNumArgs() throws IOException {
        return writeError("\"-ERR wrong number of arguments").flush();
    }

    public RespEncoder errSyntax() throws IOException {
        return writeError("ERR syntax error").flush();
    }
    public static byte[] encode(RespEncoderFunction function) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        RespEncoder encoder = new RespEncoder(baos);
        function.apply(encoder);
        return baos.toByteArray();
    }
    @FunctionalInterface
    public interface RespEncoderFunction {
        void apply(RespEncoder encoder ) throws IOException;
    }
}





