2018-4-13 11:38:00
# 项目中的短视频一条龙文档

##  1.概述
1. 短视频包含 录制、滤镜、压缩、时长剪切等功能
2. 播放视频 用 https://github.com/lipangit/JiaoZiVideoPlayer



## 2.定义
- 地址 com.zhiyicx.thinksnsplus.modules.markdown_editor
- 继承`MarkdownFragment`,诸多方法详见注释，可参考 PublishPostFragment。
- 一定要看注释！！！


## 3.使用
- 重点方法如下，具体使用请见 MarkdownFragment 中注释
```java
    initBundleDataWhenOnCreate(); 在这里获取 getArguments();
    editorPreLoad(); 编辑器初始化加载
    onAfterInitialLoad(boolean ready); ready 编辑器加载完成与否，
    preHandlePublish();
    handlePublish();
    onActivityResultForChooseCircle();
    onVisibleChange();
    openDraft();
    loadDraft();
    getDraftData();
    onMarkdownWordResult();
    canSaveDraft();
```
ps:
editor.getResultWords(boolean isPublish) 会触发编辑器内容回调 onMarkdownWordResult();
