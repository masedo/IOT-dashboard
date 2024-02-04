package com.iot.sensor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe basada en les funcions de codificació oficials (és la mateixa implementació però en Java):
 * https://github.com/Milesight-IoT/SensorDecoders/tree/main/EM_Series/EM500_Series/EM500-CO2
 */
public class PayloadDecoder {

	public static Map<String, Object> decode(int fPort, byte[] bytes) {
		return milesight(bytes);
	}

	private static Map<String, Object> milesight(byte[] bytes) {
		Map<String, Object> decoded = new HashMap<>();

		for (int i = 0; i < bytes.length; ) {
			int channelId = bytes[i++] & 0xFF;
			int channelType = bytes[i++] & 0xFF;

			switch (channelId) {
			case 0x01: // BATTERY
			if (channelType == 0x75) {
				decoded.put("battery", bytes[i] & 0xFF);
				i += 1;
			}
			break;
			case 0x03: // TEMPERATURE
				if (channelType == 0x67) {
					decoded.put("temperature", readInt16LE(bytes, i) / 10.0);
					i += 2;
				}
				break;
			case 0x04: // HUMIDITY
				if (channelType == 0x68) {
					decoded.put("humidity", (bytes[i] & 0xFF) / 2.0);
					i += 1;
				}
				break;
			case 0x05: // CO2
				if (channelType == 0x7d) {
					decoded.put("co2", readUInt16LE(bytes, i));
					i += 2;
				}
				break;
			case 0x06: // PRESSURE
				if (channelType == 0x73) {
					decoded.put("pressure", readUInt16LE(bytes, i) / 10.0);
					i += 2;
				}
				break;
			case 0x83: // TEMPERATURE CHANGE ALARM
				if (channelType == 0xd7) {
					double temperature = readInt16LE(bytes, i) / 10.0;
					double temperatureChange = readInt16LE(bytes, i + 2) / 10.0;
					String temperatureAlarm = readTemperatureAlarm(bytes[i + 4]);
					i += 5;

					Map<String, Object> tempChangeAlarm = new HashMap<>();
					tempChangeAlarm.put("temperature", temperature);
					tempChangeAlarm.put("temperatureChange", temperatureChange);
					tempChangeAlarm.put("temperatureAlarm", temperatureAlarm);

					decoded.put("temperatureChangeAlarm", tempChangeAlarm);
				}
				break;
			case 0x20: // HISTORY
				if (channelType == 0xce) {
					long timestamp = readUInt32LE(bytes, i);
					int co2 = readUInt16LE(bytes, i + 4);
					double pressure = readUInt16LE(bytes, i + 6) / 10.0;
					double temperature = readInt16LE(bytes, i + 8) / 10.0;
					double humidity = (bytes[i + 10] & 0xFF) / 2.0;
					i += 11;

					Map<String, Object> historyData = new HashMap<>();
					historyData.put("timestamp", timestamp);
					historyData.put("co2", co2);
					historyData.put("pressure", pressure);
					historyData.put("temperature", temperature);
					historyData.put("humidity", humidity);

					List<Map<String, Object>> history = (List<Map<String, Object>>) decoded.getOrDefault("history", new ArrayList<>());
					history.add(historyData);
					decoded.put("history", history);
				}
				break;
			default:
				break;
			}
		}
		return decoded;
	}

	private static int readUInt16LE(byte[] bytes, int index) {
		return ByteBuffer.wrap(bytes, index, 2).order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xFFFF;
	}

	private static int readInt16LE(byte[] bytes, int index) {
		return ByteBuffer.wrap(bytes, index, 2).order(ByteOrder.LITTLE_ENDIAN).getShort();
	}

	private static long readUInt32LE(byte[] bytes, int index) {
		return ByteBuffer.wrap(bytes, index, 4).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL;
	}

	private static String readTemperatureAlarm(byte alarmType) {
		switch (alarmType) {
		case 0:
			return "threshold alarm";
		case 1:
			return "threshold alarm release";
		case 2:
			return "mutation alarm";
		default:
			return "unknown";
		}
	}


}
