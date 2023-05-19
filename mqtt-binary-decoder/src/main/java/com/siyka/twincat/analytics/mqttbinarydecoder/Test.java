package com.siyka.twincat.analytics.mqttbinarydecoder;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.siyka.twincat.analytics.mqttbinarydecoder.analytics.SymbolStreamDecoder;

public class Test {
    
    public static void main(String[] arguments) {
        var file = new File("src/main/resources/kaitai/ema.symbol_stream");
        try {
            var fb = readFile(file).asReadOnlyBuffer();

            var symbolStream = SymbolStreamDecoder.getSymbolStream(fb);

            System.out.println(symbolStream.toString());

            for (var se : symbolStream.symbolEntries()) {
                System.out.println(se);
            }

            for (var dte : symbolStream.dataTypeEntries()) {
                System.out.println(dte);
            }

        } catch(Exception e) {}
    }

    static ByteBuffer readFile(File file) throws IOException {
        DataInputStream dataInputStream = null;
        try {
            // FIXME: this is broken for files larger than 4GiB.
            int byteCount = (int) file.length();
            
            // Always read the whole file in rather than using memory mapping.
            // Windows' file system semantics also mean that there's a period after a search finishes but before the buffer is actually unmapped where you can't write to the file (see Sun bug 6359560).
            // Being unable to manually unmap causes no functional problems but hurts performance on Unix (see Sun bug 4724038).
            // Testing in C (working on Ctags) shows that for typical source files (Linux 2.6.17 and JDK6), the performance benefit of mmap(2) is small anyway.
            // Evergreen actually searches both of those source trees faster with readFully than with map.
            // At the moment, then, there's no obvious situation where we should map the file.
            FileInputStream fileInputStream = new FileInputStream(file);
            dataInputStream = new DataInputStream(fileInputStream);
            final byte[] bytes = new byte[byteCount];
            dataInputStream.readFully(bytes);

            return ByteBuffer.wrap(bytes);
        } finally {
            if (dataInputStream != null) {
                dataInputStream.close();
            }
        }
    }

}
