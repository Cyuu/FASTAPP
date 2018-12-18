## FAST Android APP

-------------

#### Framework

- dtchooser
- swipy
- com.android.support:multidex:1.0.3 // Caused By failed to build on Galaxy S4
- okhttputils
- eventbus
- butterknife
- loadingviewlib
- picasso
- getui
- FlycoTabLayout_Lib

#### Abandoned Framework
- RabbitMQ
- Socket

-------------

#### Development Process Record

+ Begin From: 2017.9.13
+ version：1.0
+ version：1.0


##### 2018.2.1
- 1 雷达，光纤view的自定义绘制，增加：动态改变帧数的设置

##### 2018.12.6
- 1 基本调试完成，demo完成
-------------------
### 基类
##### BaseActivity 
1. 通用的系统配置（状态栏，全屏配置什么的）
2. 方法拆分（onCreate拆成 initView、initListener等等）
3. 业务初始化/销毁动作（推送、事件总线EventBus\otto什么的）
4. 写一些共用方法（比如跳转方法）
##### BaseLazyFragment 懒加载Fragment
- 懒加载
##### 网络
 - HttpUtil
 - 计划：HttpUtil --> Retrofit2
 
##### 事件总线
 - EventBus
 
##### 视频
- 海康sdk

-------------

### 业务解析

#### MainActivity (此页面接收实时数据，实时刷新tab上的告警数目，和列表)
##### 分成以下类别：Fragment: Fiber  Radar  Video  Face  Ipr  Phone
###### 根据业务分类
 - IPCFragment: 
    展示所有摄像头设备的设备列表，要展示各种设备参数。 onItemClick --> VideoActivity
        
 - FiberFragment:  
    只展示所有光纤告警的列表, 背景为红色。 onItemClick --> FiberActivity
    设备名称，所属区域，告警级别，告警时间，告警状态，
    上行参数：
    返回参数：
    
    操作：告警恢复，
    上行参数：
    返回参数：
    
 - RadarFragment: 
    只展示所有雷达监测到的目标入侵的告警列表，背景为红色。 onItemClick --> RadarActivity

 - FaceFragment:   （表有问题，）
    展示所有<人脸识别>的历史记录列表，有告警的场景，背景为红色，无告警的场景，背景为白色。 onItemClick --> FaceActivity
    
 - PlateFragment:   （表有问题，）
    只展示<车牌|卡口>的告警列表。 onItemClick --> IprActivity
    
 - PhoneFragment: 
    只展示<手机信号检测>的告警列表。 onItemClick --> PhoneActivity

#### XXXDetailActivity (**此页面不接收MQ的实时数据，只展示根据父层id获取到的数据**)

 - IPCActivity:
    查看该摄像头的 
    1 实时视频    (via 海康sdk)
    2 可以查看录像(via 海康sdk)
    3 根据alarmTime，设置回放的开始时间和结束时间，告警时间 （前三分钟，后三分钟）。(-3，+3);

 - FiberActivity: 
    展示产生光纤告警的
    1 光纤告警数据
    2 实时视频(via 海康sdk)
    3 可以查看录像(via 海康sdk)
    
 - RadarActivity: 
    展示产生雷达告警的
    1 雷达告警数据
    2 实时视频(via 海康sdk)
    3 可以查看录像(via 海康sdk)
    
 - FaceActivity:  
    展示产生人脸识别记录产生的
    1 人脸对应的用户数据数据 
    2 人脸图片对比（2张ImageView， url）
    3 实时视频(via 海康sdk)
    4 可以查看录像(via 海康sdk)

 - PlateActivity:   
    展示产生车牌记录
    1 车牌数据
    2 实时视频(via 海康sdk)
    3 可以查看录像(via 海康sdk)
    
 - PhoneActivity: 
    展示产生手机微信号告警的
    1 微信号告警数据
    2 实时视频(via 海康sdk)
    3 可以查看录像(via 海康sdk)
        
-------------
### 一些问题：
##### 1. 解决SurfaceView调用setZOrderOnTop(true)遮挡其他控件
- Fragment中使用Toolbar， setHasOptionsMenu(true);
- setZOrderOnTop(true)
- setZOrderMediaOverlay(true)

##### 2. 推送条数限制和推送的开关

##### 3. 多Tab不能填满屏幕宽度

-------------

### 报文：
##### 告警列表报文：

###### alarmStatus
1. 0   新告警
2. 1   已恢复
3. 2   已取消
4. 256 已确认已取消
5. 257 已确认
6. 258 已确认已恢复

================
后续优化：
详情页面,比如车牌，人脸页面，左滑/右滑，切换列表页的数据

