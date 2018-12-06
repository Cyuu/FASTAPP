快捷键：
1 Ctrl + Q,  查看类的定义和说明，并展示在右侧的窗口，代替鼠标悬浮在类上方。
2 Ctrl + F12, 快读浏览该文件的声明和方法
3 

FAST项目的手机版本：
1 框架，基于蓄电池防盗app
2 

-------------------
<h1>开发过程记录</h1>
<h4>开始时间: 2017.9.13</h4>

<h4>项目名称：FASTAPP</h4>
<h4>包名:com.thdz.fast</h4>
<h4>版本：1.0  版本号： 1</h4>
<h4>签名：thdz.jks</h4>

-------------------
1 Json格式的问题：需要server端修改成合理的json格式，多了一层双引号
2 消息队列

--------------
--bug:
 1 floatingButton这个控件，需要重新布局，不能直接放在RelativeLayout里
 2 乱码: 发送前，转码：utf-8

-------------------
### 2018.2.1
后续任务：
1 重新做logo， 优先级：不高
2 写计划
3 根据计划按步骤进行开发
4 计划前，可以先研究：
  雷达，光纤view的自定义绘制，增加：动态改变帧数的设置

----------------------
BaseActivity的封装：
<p>
1、 通用的系统配置（状态栏，全屏配置什么的）<br/>
2、方法拆分（onCreate拆成 initView、initListener等等）<br/>
3、业务初始化/销毁动作（推送、事件总线EventBus\otto什么的）<br/>
4、写一些共用方法（比如跳转方法）<br/>
====
---------------
基本ok，详见BaseActivity代码，其他Activity基类，属于试验版本
</p>
-------------
封装网络请求和异步任务
retrofit2 + okhttp


翻译
---------
retro: 制动/减速火箭， 重新流行
---------
fit: 安装，适合，装修
---------
Retrofit： 改造
---------

=========================== 
业务解析：
1 页面流程
(1) LoginActivity
(2) MainActivity (此页面接收MQ的实时数据，实时刷新tab上的告警数目，和列表)
  (2.1)  Fragment: Fiber  Radar  Video  Face  Ipr  Phone
    (**所有“只展示”的数据，都：不根据场景(地点|设备)进行分类展示**)
    
    针对所有告警列表页 XXXFragment ：
        页面：下拉刷新，上拉分页。
        item长按 --> 恢复告警
        
    IPCFragment: 
        展示所有摄像头设备的设备列表，要展示各种设备参数。 onItemClick --> VideoActivity
            
    FiberFragment:  
        只展示所有光纤告警的列表, 背景为红色。 onItemClick --> FiberActivity
        设备名称，所属区域，告警级别，告警时间，告警状态，
        上行参数：
        返回参数：
        
        操作：告警恢复，
        上行参数：
        返回参数：
        
    RadarFragment: 
        只展示所有雷达监测到的目标入侵的告警列表，背景为红色。 onItemClick --> RadarActivity
    
    FaceFragment:   （表有问题，）
        展示所有<人脸识别>的历史记录列表，有告警的场景，背景为红色，无告警的场景，背景为白色。 onItemClick --> FaceActivity
        
    PlateFragment:   （表有问题，）
        只展示<车牌|卡口>的告警列表。 onItemClick --> IprActivity
        
    PhoneFragment: 
        只展示<手机信号检测>的告警列表。 onItemClick --> PhoneActivity

(3) XXXSubActivity (**此页面不接收MQ的实时数据，只展示根据父层id获取到的数据**)

    IPCActivity:
        查看该摄像头的 
        1 实时视频    (via 海康sdk)
        2 可以查看录像(via 海康sdk)
        3 根据alarmTime，设置回放的开始时间和结束时间，告警时间 （前三分钟，后三分钟）。(-3，+3);
    
    FiberActivity: 
        展示产生光纤告警的
        1 光纤告警数据
        2 实时视频(via 海康sdk)
        3 可以查看录像(via 海康sdk)
        
    RadarActivity: 
        展示产生雷达告警的
        1 雷达告警数据
        2 实时视频(via 海康sdk)
        3 可以查看录像(via 海康sdk)
        
    FaceActivity:  
        展示产生人脸识别记录产生的
        1 人脸对应的用户数据数据 
        2 人脸图片对比（2张ImageView， url）
        3 实时视频(via 海康sdk)
        4 可以查看录像(via 海康sdk)

    PlateActivity:   
        展示产生车牌记录
        1 车牌数据
        2 实时视频(via 海康sdk)
        3 可以查看录像(via 海康sdk)
        
    PhoneActivity: 
        展示产生手机微信号告警的
        1 微信号告警数据
        2 实时视频(via 海康sdk)
        3 可以查看录像(via 海康sdk)
        
 ------------------------------
 Fragment中使用Toolbar， setHasOptionsMenu(true);

个推：
cid: ae144b04f8aa80badfda01da235b94a4

 解决SurfaceView调用setZOrderOnTop(true)遮挡其他控件
setZOrderOnTop(true)
setZOrderMediaOverlay(true)

---------------
待完成：
1 雷达模块
2 人脸模块
3 优化海康SDK封装
4 

----------------
告警列表报文：
alarmStatus
0   新告警
1   已恢复
2   已取消
256 已确认已取消
257 已确认
258 已确认已恢复

