package net.jueb.util4j.beta.tools.taskQueue;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 顺序任务执行队列
 * 
 * @author Administrator
 */
public class OrderTaskQueue {
	public static final Logger log = LoggerFactory.getLogger(OrderTaskQueue.class);
	protected final ConcurrentLinkedQueue<Task> tasks = new ConcurrentLinkedQueue<Task>();
	protected final TaskRunner runner;// 运行者
	protected final CountMonitor cm=new CountMonitor();
	public static long CountMonitorInterval=60*1000;
	
	public OrderTaskQueue(String name) {
		runner = new TaskRunner(name);
	}

	public String getName() {
		return runner.getName();
	}

	public void addTask(Task task) {
		if (task != null) {
			tasks.add(task);
			runner.wakeUpIfSleep();
		}
	}

	public CountMonitor getCountMonitor() {
		return cm;
	}

	public int taskCount() {
		return tasks.size();
	}

	public boolean removeTask(Task task) {
		return tasks.remove(task);
	}

	public void start() {
		runner.start();
	}

	public void stop() {
		runner.shutdown();
	}

	public static interface Task {

		public void action();

		public String name();
	}

	/**
	 * 任务对象
	 * 
	 * @author Administrator
	 */
	class TaskObj {
		private long startTime=0;// 开始时间
		private long endTime = 0;// 结束时间
		private Task task;

		public TaskObj(Task task) {
			this.task = task;
		}

		public void start() {
			startTime = System.currentTimeMillis();
			try {
				task.action();
			} catch (Exception e) {
				log.debug("task error[" + task.getClass() + "]:"+ e.getMessage());
			}
			endTime = System.currentTimeMillis();
		}

		public long getStartTime() {
			return startTime;
		}

		public Task getTask() {
			return task;
		}

		public void setTask(Task task) {
			this.task = task;
		}

		public long getEndTime() {
			return endTime;
		}
	}

	/**
	 * 线程执行者
	 * 
	 * @author Administrator
	 *
	 */
	class TaskRunner {
		private TaskObj currentTask;// 当前任务对象
		private final String name;
		private RunnnerCore runnnerCore;// 运行核心线程
		private boolean isActive;
		public TaskRunner(String name) {
			this.name = name;
		}

		public void start() {
			if (runnnerCore == null) {
				runnnerCore = new RunnnerCore();
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			runnnerCore.setDaemon(true);
			runnnerCore.setName(name);
			runnnerCore.start();
		}

		private class RunnnerCore extends Thread {
			private CountDownLatch latch;
			private boolean shutdown;// 关闭=false

			@Override
			public void run() {
				isActive = true;
				try {
					while (!shutdown) {
						Task task = tasks.poll();
						if (task == null) {// 线程睡眠
//							long awaitStartTime = System.currentTimeMillis();
							// log.debug("tasks.isEmpty(),TaskRunner sleep……");
							latch = new CountDownLatch(1);
							latch.await();
							latch = null;
//							long awaitTime = System.currentTimeMillis()- awaitStartTime;
							// log.debug("TaskRunner WakeUp,sleepTime="+
							// awaitTime + " Millis");
						} else {// 线程被外部条件唤醒
							currentTask = new TaskObj(task);
							cm.taskRunBefore(currentTask);
							currentTask.start();
							cm.taskRunAfter(currentTask);
							currentTask = null;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				isActive = false;
			}

			/**
			 * 如果是睡眠,则唤醒
			 */
			public void wakeUpIfSleep() {
				if (latch != null) {// 如果线程睡眠则唤醒
					latch.countDown();
				}
			}

			public void shutdown() {
				this.shutdown = true;
			}
		}

		/**
		 * 跳过当前任务执行下面的任务
		 */
		public void skipCurrentTask(TaskObj task) {
			if (task == currentTask) {
				RunnnerCore oldCore = this.runnnerCore;
				oldCore.setName(name + "_old");
				RunnnerCore newCore = new RunnnerCore();
				this.runnnerCore = newCore;
				start();
				oldCore.shutdown();// 优雅推出线程
				log.warn("skip task" + task.toString());
			}
		}

		public boolean isActive() {
			return isActive;
		}

		public void shutdown() {
			this.runnnerCore.shutdown();
		}

		/**
		 * 如果是睡眠,则唤醒
		 */
		public void wakeUpIfSleep() {
			if (!tasks.isEmpty() && runnnerCore != null) {
				runnnerCore.wakeUpIfSleep();
			}
		}

		public TaskObj getCurrentTask() {
			return currentTask;
		}

		public String getName() {
			return name;
		}
	}
	
	/**
	 * 效率监视
	 * @author Administrator
	 */
	class CountMonitor{
		private boolean enable=true;
		private final HashMap<String, MonitorEntry> entrys=new HashMap<String,MonitorEntry>();
		private final HashMap<String, MonitorEntry> interval=new HashMap<String,MonitorEntry>();//区间监测
		private long lastCheckTime;//上一次检查时间
		private long checkInterval=CountMonitorInterval;//监测时间间隔
		private SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss sss");
		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

		public void taskRunBefore(TaskObj obj)
		{
			if(!enable)
			{
				return;
			}
			if(lastCheckTime==0)
			{
				lastCheckTime=System.currentTimeMillis();
			}
			long currentTime=System.currentTimeMillis();
			if(currentTime-lastCheckTime>checkInterval)
			{
				syso(interval,getName()+"区间监视["+sdf.format(new Date(lastCheckTime))+","+sdf.format(new Date(currentTime))+"]");
				lastCheckTime=currentTime;
				interval.clear();
				syso(entrys,getName()+"总监视");
			}
		}
		
		public void taskRunAfter(TaskObj obj)
		{
			if(!enable)
			{
				return;
			}
			long time=obj.getEndTime()-obj.getStartTime();//脚本耗时
			String name=obj.getTask().name();
			//更新区间监测数据
			long currentTime=System.currentTimeMillis();
			if(currentTime-lastCheckTime<=checkInterval)
			{
				if(!interval.containsKey(name))
				{
					interval.put(name, new MonitorEntry(name));
				}
				MonitorEntry entry=interval.get(name);
				entry.setTotalCount(entry.getTotalCount()+1);
				entry.setTotalTime(entry.getTotalTime()+time);
				if(entry.getMaxTime()<time)
				{
					entry.setMaxTime(time);
				}
			}
			//更新总监测数据
			
			if(!entrys.containsKey(name))
			{
				entrys.put(name, new MonitorEntry(name));
			}
			MonitorEntry entry=entrys.get(name);
			entry.setTotalCount(entry.getTotalCount()+1);
			entry.setTotalTime(entry.getTotalTime()+time);
			if(entry.getMaxTime()<time)
			{
				entry.setMaxTime(time);
			}
		}
		
		
		public void syso(HashMap<String, MonitorEntry> entrys,String title)
		{
			StringBuffer sb=new StringBuffer();
			sb.append("\n");
			sb.append("****************"+title+"********************\n");
			long allCount=0;
			long allTimes=0;
			for(String name:entrys.keySet())
			{
				MonitorEntry entry=entrys.get(name);
				allCount+=entry.getTotalCount();
				allTimes+=entry.getTotalTime();
				sb.append("任务"+name+"执行总次数:"+entry.getTotalCount()+",总耗时"+entry.getTotalTime()+",平均耗时"+entry.getAvgTime()+",最大耗时"+entry.getMaxTime()+"\n");
			}
			sb.append("总计执行任务次数:"+allCount+"\n");
			sb.append("总计执行脚任务耗时:"+allTimes+"\n");
			sb.append("总计任务平均耗时:"+allTimes/allCount+"\n");
			sb.append("*************************************************");
			log.info(sb.toString());
		}
		
		/**
		 * 监视对象
		 * @author Administrator
		 */
		class MonitorEntry{
			private final String name;
			private long totalTime;//总时间
			private long totalCount;//总执行次数
			private long maxTime;//最大时间
			public MonitorEntry(String name) {
				this.name=name;
			}
			public String getName() {
				return name;
			}
			public long getTotalTime() {
				return totalTime;
			}
			public void setTotalTime(long totalTime) {
				this.totalTime = totalTime;
			}
			public long getTotalCount() {
				return totalCount;
			}
			public void setTotalCount(long totalCount) {
				this.totalCount = totalCount;
			}
			public long getMaxTime() {
				return maxTime;
			}
			public void setMaxTime(long maxTime) {
				this.maxTime = maxTime;
			}
			public long getAvgTime() {
				return totalTime/totalCount;
			}
		}
	}
}