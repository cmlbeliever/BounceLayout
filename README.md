# BounceLayout
最近任务比较少，闲来时间就来研究了android事件传播机制。根据总结分析的结果，打造出万能弹性layout，支持内嵌可滚动view！ 
先看图片（笔记本分辨率不兼容，将就看看） 
## Demo 图片
![](https://github.com/cmlbeliever/BounceLayout/demo/demo.gif)

#弹性layout的核心内容就分析:
- 1、由于内嵌可滚动view会导致事件冲突，所以在在移动时需要判断事件是否由内嵌的viewgroup消费。 
- 2、在view布局处理好后，将可滚动的view信息保存起来
- 3、当用户按下时，根据按下坐标查找是否在可滚动viewgroup内
- 4、当用户在垂直方向移动，则判断touchView是否可以在垂直方向移动，如果可以，则事件交给touchView处理，否则进行layout的移动
- 5、当用户在水平方向移动，则判断touchView是否可以在水平方向移动，如果可以，则事件交给touchView处理，否则进行layout的移动