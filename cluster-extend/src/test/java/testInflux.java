/**
 * Created by fantasy on 2016/9/5.
 */

import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class testInflux {
	@org.junit.Test
	public void TestInflux() {
		InfluxDB db = InfluxDBFactory.connect("http://192.168.253.128:8086", "root", "");
		System.out.println("here");
		while (true) {
			BatchPoints batchPoints = BatchPoints.database("yawl_snapshot").tag("async", "true")
					.retentionPolicy("default").consistency(InfluxDB.ConsistencyLevel.ALL).build();
			Point casePt = Point.measurement("case").time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
					.addField("number", Math.round(Math.sin(System.currentTimeMillis()) * 40 + 200))
					.addField("interval", 5).addField("speed", 211 / 5).build();
			batchPoints.point(casePt);
			db.write(batchPoints);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
