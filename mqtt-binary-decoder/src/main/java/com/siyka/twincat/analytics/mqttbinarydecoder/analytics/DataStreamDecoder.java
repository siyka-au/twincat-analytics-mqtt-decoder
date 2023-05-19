package com.siyka.twincat.analytics.mqttbinarydecoder.analytics;

import static com.siyka.twincat.analytics.mqttbinarydecoder.utils.Decoders.getGuid;
import static com.siyka.twincat.analytics.mqttbinarydecoder.utils.Decoders.getUnsignedByte;
import static com.siyka.twincat.analytics.mqttbinarydecoder.utils.Decoders.getUnsignedInt;
import static com.siyka.twincat.analytics.mqttbinarydecoder.utils.Decoders.skip;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.ArrayList;

import com.siyka.twincat.analytics.mqttbinarydecoder.utils.WindowsTime;

public class DataStreamDecoder {

    public static DataStream getDataStream(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        var header = getHeader(buffer);

        var data = new ArrayList<DataPoint>();

        var startPosition = buffer.position();

        // for (int i = 0; i < header.getSamples(); i++) {
        //     var timestamp = WindowsTime.toInstant(buffer.getLong());

        //     symbolService.getSymbolStreamByAssetName(assetName).ifPresent(s -> {
        //         for (var se : s.getSymbolEntries()) {
        //             var offset = (int)(se.getIndexOffset() & 0xffff) + buffer.position();
        //             switch(se.getDataType()) {
        //                 case BIT:
        //                     data.add(new DataPoint(se.getName(), timestamp, (boolean)(buffer.get(offset) > 0)));
        //                     break;
        //                 case INT8:
        //                     data.add(new DataPoint(se.getName(), timestamp, buffer.get(offset)));
        //                     break;
        //                 case INT16:
        //                     data.add(new DataPoint(se.getName(), timestamp, buffer.getShort(offset)));
        //                     break;
        //                 case INT32:
        //                     data.add(new DataPoint(se.getName(), timestamp, buffer.getInt(offset)));
        //                     break;
        //                 case INT64:
        //                     data.add(new DataPoint(se.getName(), timestamp, buffer.getLong(offset)));
        //                     break;
        //                 case UINT8:
        //                     data.add(new DataPoint(se.getName(), timestamp, getUnsignedByte(buffer, offset)));
        //                     break;
        //                 case UINT16:
        //                     data.add(new DataPoint(se.getName(), timestamp, getUnsignedShort(buffer, offset)));
        //                     break;
        //                 case UINT32:
        //                     switch (se.getTypeName()) {
        //                         case "UDINT":
        //                             data.add(new DataPoint(se.getName(), timestamp, getUnsignedInt(buffer, offset)));
        //                             break;
        //                         case "TIME":
        //                             var time = getUnsignedInt(buffer, offset);
        //                             data.add(new DataPoint(se.getName(), timestamp, Duration.ofMillis(time)));
        //                             break;
        //                     }
        //                     break;
        //                 case UINT64:
        //                     break;
        //                 case REAL32:
        //                     data.add(new DataPoint(se.getName(), timestamp, buffer.getFloat(offset)));
        //                     break;
        //                 case REAL64:
        //                     data.add(new DataPoint(se.getName(), timestamp, buffer.getDouble(offset)));
        //                     break;
        //                 case REAL80:
        //                     break;
        //                 case BIGTYPE:
        //                     switch (se.getTypeName()) {
        //                         case "LTIME":
        //                             var ltime = buffer.getLong(offset);
        //                             data.add(new DataPoint(se.getName(), timestamp, Duration.ofNanos(ltime)));
        //                             break;
        //                     }
        //                     break;
        //                 case STRING:
        //                     {
        //                         var bytes = new byte[(int) se.getSize()];
        //                         buffer.get(offset, bytes);
        //                         data.add(new DataPoint(se.getName(), timestamp, new String(bytes, StandardCharsets.US_ASCII)));
        //                     }
        //                     break;
        //                 case WSTRING:
        //                     {
        //                         var bytes = new byte[(int) se.getSize()];
        //                         buffer.get(offset, bytes);
        //                         data.add(new DataPoint(se.getName(), timestamp, new String(bytes, StandardCharsets.UTF_16LE)));
        //                     }
        //                     break;
        //                 case VOID:
        //                     break;
        //                 case MAXTYPES:
        //                     break;
        //                 default:
        //                     break;
        //             }
        //         }
        //         buffer.position(startPosition + (int) (header.getSampleHeaderSize() + header.getDataSize()));
        //     });
        // }

        return new DataStream(header, null);
    }

    private static DataStream.Header getHeader(ByteBuffer buffer) {
        var majorVersion = buffer.get();
        var minorVersion = buffer.get();
        var headerSize = getUnsignedByte(buffer);
        var sampleHeaderSize = getUnsignedByte(buffer);
        var dataSize = getUnsignedInt(buffer);
        var cycleTime = WindowsTime.toDuration(getUnsignedInt(buffer));
        var flags = decodeFlags(buffer.getInt());
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

        return new DataStream.Header(majorVersion, minorVersion,
                headerSize, sampleHeaderSize, dataSize,
                cycleTime,
                flags,
                hash,
                samples, sampleStartTime, sampleStopTime);
    }

    private static DataStream.Flags decodeFlags(int data) {
        var hasHeadTimestamp = (data & 0x00000001) > 0; // 1;
        var hasSampleTimestamp = (data & 0x00000002) > 0; // 1;
        var dcTime = (data & 0x00000004) > 0; // 1;
        var compressionMethod = switch(data & 0x00000070) {
            case 0 -> DataStream.CompressionMethod.NONE;
            case 1 -> DataStream.CompressionMethod.RUN_LENGTH;
            case 2 -> DataStream.CompressionMethod.RESERVED;
            default -> DataStream.CompressionMethod.NONE;
        };

        return new DataStream.Flags(hasHeadTimestamp, hasSampleTimestamp, dcTime, compressionMethod);
    }

    private static Object getSamples(ByteBuffer buffer) {
        return null;
    }

}
