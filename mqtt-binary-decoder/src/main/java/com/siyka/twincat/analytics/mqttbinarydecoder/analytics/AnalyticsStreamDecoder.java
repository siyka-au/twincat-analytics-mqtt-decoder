package com.siyka.twincat.analytics.mqttbinarydecoder.analytics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import org.springframework.integration.core.GenericTransformer;

import com.siyka.twincat.analytics.mqttbinarydecoder.SymbolService;

public class AnalyticsStreamDecoder implements GenericTransformer<byte[], AnalyticsStream> {

    private SymbolService symbolService;
    private String assetName;

    public AnalyticsStreamDecoder(SymbolService symbolService, String assetName) {
        this.symbolService = symbolService;
        this.assetName = assetName;
    }


    public static String print(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (byte b : bytes) {
            sb.append(String.format("0x%02X ", b));
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public AnalyticsStream transform(byte[] source) {
        var buffer = ByteBuffer.wrap(source).asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN);
        var header = getAnalyticsStreamHeader(buffer);

        var data = new ArrayList<DataPoint>();

        buffer.mark();

        for (int i = 0; i < header.getSamples(); i++) {
            var timestamp = WindowsTime.toInstant(buffer.getLong());

            symbolService.getSymbolStreamByAssetName(assetName).ifPresent(s -> {
                for (var se : s.getSymbolEntries()) {
                    var offset = (int)(se.getIndexOffset() & 0xffff) + buffer.position();
                    switch(se.getDataType()) {
                        case BIT:
                            data.add(new DataPoint(se.getName(), timestamp, (boolean)(buffer.get(offset) > 0)));
                            break;
                        case INT8:
                            data.add(new DataPoint(se.getName(), timestamp, buffer.get(offset)));
                            break;
                        case INT16:
                            data.add(new DataPoint(se.getName(), timestamp, buffer.getShort(offset)));
                            break;
                        case INT32:
                            data.add(new DataPoint(se.getName(), timestamp, buffer.getInt(offset)));
                            break;
                        case INT64:
                            data.add(new DataPoint(se.getName(), timestamp, buffer.getLong(offset)));
                            break;
                        case UINT8:
                            data.add(new DataPoint(se.getName(), timestamp, getUnsignedByte(buffer, offset)));
                            break;
                        case UINT16:
                            data.add(new DataPoint(se.getName(), timestamp, getUnsignedShort(buffer, offset)));
                            break;
                        case UINT32:
                            switch (se.getTypeName()) {
                                case "UDINT":
                                    data.add(new DataPoint(se.getName(), timestamp, getUnsignedInt(buffer, offset)));
                                    break;
                                case "TIME":
                                    var time = getUnsignedInt(buffer, offset);
                                    data.add(new DataPoint(se.getName(), timestamp, Duration.ofMillis(time)));
                                    break;
                            }
                            break;
                        case UINT64:
                            break;
                        case REAL32:
                            data.add(new DataPoint(se.getName(), timestamp, buffer.getFloat(offset)));
                            break;
                        case REAL64:
                            data.add(new DataPoint(se.getName(), timestamp, buffer.getDouble(offset)));
                            break;
                        case REAL80:
                            break;
                        case BIGTYPE:
                            switch (se.getTypeName()) {
                                case "LTIME":
                                    var ltime = buffer.getLong(offset);
                                    data.add(new DataPoint(se.getName(), timestamp, Duration.ofNanos(ltime)));
                                    break;
                            }
                            break;
                        case STRING:
                            {
                                var bytes = new byte[(int) se.getSize()];
                                buffer.get(offset, bytes);
                                data.add(new DataPoint(se.getName(), timestamp, new String(bytes, StandardCharsets.US_ASCII)));
                            }
                            break;
                        case WSTRING:
                            {
                                var bytes = new byte[(int) se.getSize()];
                                buffer.get(offset, bytes);
                                data.add(new DataPoint(se.getName(), timestamp, new String(bytes, StandardCharsets.UTF_16LE)));
                            }
                            break;
                        case VOID:
                            break;
                        case MAXTYPES:
                            break;
                        default:
                            break;
                    }
                }
                buffer.reset();
                buffer.position(buffer.position() + (int) header.getSampleHeaderSize() + (int) header.getDataSize());
            });
        }

        return new AnalyticsStream(header, data);
    }

    private static AnalyticsStreamHeader getAnalyticsStreamHeader(ByteBuffer buffer) {
        var majorVersion = buffer.get();
        var minorVersion = buffer.get();
        var headerSize = getUnsignedByte(buffer);
        var sampleHeaderSize = getUnsignedByte(buffer);
        var dataSize = getUnsignedInt(buffer);
        var cycleTime = WindowsTime.toDuration(getUnsignedInt(buffer));
        skip(buffer, 4);
        var hash = getGuid(buffer);

        var samples = 1L;
        var sampleStartTime = Instant.now();
        var sampleStopTime = sampleStartTime;

        if (majorVersion == 1 && minorVersion == 1) {
            samples = getUnsignedInt(buffer);
            skip(buffer, 4);
            sampleStartTime = WindowsTime.toInstant(buffer.getLong());
            sampleStopTime = WindowsTime.toInstant(buffer.getLong());
        }

        return new AnalyticsStreamHeader(majorVersion, minorVersion,
                headerSize, sampleHeaderSize, dataSize,
                cycleTime,
                new AnalyticsStreamFlag(),
                hash,
                samples, sampleStartTime, sampleStopTime);
    }

}
