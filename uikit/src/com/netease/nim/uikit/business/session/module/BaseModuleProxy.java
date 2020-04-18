package com.netease.nim.uikit.business.session.module;

import java.io.File;

/**
 * 会话窗口提供给子模块的代理接口。
 */
public interface BaseModuleProxy {

    boolean sendMessage(File file);

}
