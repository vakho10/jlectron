package com.github.vakho10.jcef.handler;

import com.github.vakho10.jcef.MainFrame;
import lombok.extern.slf4j.Slf4j;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import javax.swing.*;

@Slf4j
public class MessageRouterHandler extends CefMessageRouterHandlerAdapter {

    private final MainFrame mainFrame;

    public MessageRouterHandler(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public boolean onQuery(CefBrowser browser, CefFrame frame, long query_id, String request,
                           boolean persistent, CefQueryCallback callback) {
        if ("closeApp".equals(request)) {
            log.debug("Closing app via Angular");
            SwingUtilities.invokeLater(mainFrame::exitApp);
            callback.success("App closed");
            return true;
        }
        // Not handled.
        return false;
    }
}
