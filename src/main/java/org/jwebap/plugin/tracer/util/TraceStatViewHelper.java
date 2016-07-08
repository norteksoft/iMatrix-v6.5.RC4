package org.jwebap.plugin.tracer.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.jwebap.plugin.tracer.FrequencyAnalyser;
import org.jwebap.plugin.tracer.TraceFrequency;
import org.jwebap.util.Assert;


/**
 * 轨迹信息统计视图助手类
 * 
 * @author leadyu(yu-lead@163.com)
 * @since Jwebap 0.5
 * @date  Feb 19, 2008
 */
public class TraceStatViewHelper {
	/** 轨迹信息统计分析器 */
	private FrequencyAnalyser analyser;

	public TraceStatViewHelper(FrequencyAnalyser analyser) {
		Assert.assertNotNull(analyser, "stat analyser is null.");
		this.analyser = analyser;
	}
	
	public JSONObject processJson(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
		
		List datas = new ArrayList(analyser.getTraceFrequencies().values());

		int pageSize = 20;
		int start = 0;
		int total = datas.size();
		String strStartNum = request.getParameter("start");
		String strPageSize = request.getParameter("limit");
		try {
			start = Integer.parseInt(strStartNum);
			pageSize = Integer.parseInt(strPageSize);
		} catch (Exception e) {
		}

		int toIndex = start + pageSize > total ? total : start + pageSize;

		// 排序
		String sort = request.getParameter("sort");
		String dir = request.getParameter("dir");
		if (dir == null) {
			dir = "DESC";
		}

		if ("average".equals(sort)) {
			Collections.sort(datas, new AverageActiveTimeComparator(dir));
		}else if ("min".equals(sort)) {
			Collections.sort(datas, new MinActiveTimeComparator(dir));
		}else if ("max".equals(sort)) {
			Collections.sort(datas, new MaxActiveTimeComparator(dir));
		} else {
			Collections.sort(datas, new FrequencyComparator(dir));
		}

		JSONObject json = new JSONObject();
		json.put("totalCount", total);
		json.put("stat", datas.subList(start, toIndex));
		return json;
	}
	
}


class AverageActiveTimeComparator implements Comparator {
	private String dir;

	AverageActiveTimeComparator(String dir) {
		this.dir = dir;
	}

	public int compare(Object o1, Object o2) {
		if (!(o1 instanceof TraceFrequency) || !(o2 instanceof TraceFrequency)) {
			return -1;
		} else {
			TraceFrequency so1 = (TraceFrequency) o1;
			TraceFrequency so2 = (TraceFrequency) o2;
			int t1 = so1.getFrequency() <= 0 ? 0 : (int) (so1
					.getTotalActiveTime() / (long) so1.getFrequency());
			int t2 = so2.getFrequency() <= 0 ? 0 : (int) (so2
					.getTotalActiveTime() / (long) so2.getFrequency());
			return ("DESC".equals(dir) ? t2 - t1 : t1 - t2);
		}
	}
}

class FrequencyComparator implements Comparator {

	private String dir;

	FrequencyComparator(String dir) {
		this.dir = dir;
	}

	public int compare(Object o1, Object o2) {
		if (!(o1 instanceof TraceFrequency) || !(o2 instanceof TraceFrequency)) {
			return -1;
		} else {
			TraceFrequency so1 = (TraceFrequency) o1;
			TraceFrequency so2 = (TraceFrequency) o2;
			return ("DESC".equals(dir) ? so2.getFrequency()
					- so1.getFrequency() : so1.getFrequency()
					- so2.getFrequency());
		}
	}
}

class MaxActiveTimeComparator implements Comparator {

	private String dir;

	MaxActiveTimeComparator(String dir) {
		this.dir = dir;
	}

	public int compare(Object o1, Object o2) {
		if (!(o1 instanceof TraceFrequency) || !(o2 instanceof TraceFrequency)) {
			return -1;
		} else {
			TraceFrequency so1 = (TraceFrequency) o1;
			TraceFrequency so2 = (TraceFrequency) o2;
			int t1 = (int) so1.getMaxActiveTime();
			int t2 = (int) so2.getMaxActiveTime();
			return ("DESC".equals(dir) ? t2 - t1 : t1 - t2);
		}
	}
}

class MinActiveTimeComparator implements Comparator {

	private String dir;

	MinActiveTimeComparator(String dir) {
		this.dir = dir;
	}

	public int compare(Object o1, Object o2) {
		if (!(o1 instanceof TraceFrequency) || !(o2 instanceof TraceFrequency)) {
			return -1;
		} else {
			TraceFrequency so1 = (TraceFrequency) o1;
			TraceFrequency so2 = (TraceFrequency) o2;
			int t1 = (int) so1.getMinActiveTime();
			int t2 = (int) so2.getMinActiveTime();
			return ("DESC".equals(dir) ? t2 - t1 : t1 - t2);
		}
	}
}