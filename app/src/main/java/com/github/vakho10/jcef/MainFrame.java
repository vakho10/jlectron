package com.github.vakho10.jcef;

import com.github.vakho10.jcef.handler.MessageRouterHandler;
import com.github.vakho10.jcef.handler.MessageRouterHandlerEx;
import lombok.extern.slf4j.Slf4j;
import org.cef.CefApp;
import org.cef.CefApp.CefAppState;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefMenuModel;
import org.cef.handler.CefAppHandlerAdapter;
import org.cef.handler.CefContextMenuHandlerAdapter;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Minimal JCEF window to display Spring Boot web content.
 */
@Slf4j
public class MainFrame extends JFrame {

    private CefApp cefApp;

    public MainFrame(int port, boolean isProduction) {
        // Initialize CefApp and handle termination
        CefApp.addAppHandler(new CefAppHandlerAdapter(null) {
            @Override
            public void stateHasChanged(CefAppState state) {
                if (state == CefAppState.TERMINATED) System.exit(0);
            }
        });
        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = false; // no OSR
        cefApp = CefApp.getInstance(settings);

        // Create client and browser
        CefClient client = cefApp.createClient();

        CefMessageRouter msgRouter = CefMessageRouter.create();
        msgRouter.addHandler(new MessageRouterHandler(this), true);
        msgRouter.addHandler(new MessageRouterHandlerEx(client), false);
        client.addMessageRouter(msgRouter);

        if (isProduction) {
            client.addContextMenuHandler(new CefContextMenuHandlerAdapter() {
                @Override
                public void onBeforeContextMenu(CefBrowser browser, CefFrame frame, CefContextMenuParams params, CefMenuModel model) {
                    model.clear(); // disables the context menu
                }
            });
        }

        CefBrowser browser = client.createBrowser("localhost:" + port, false, false);

        // Add browser UI to JFrame
        getContentPane().add(browser.getUIComponent());
        setSize(800, 600);
        setLocationRelativeTo(null); // Center the window on screen
        setVisible(true); // Show the window

        log.info("Launched JCEF on port: {}", port);

        // Exit CEF and Spring Boot when window closes
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApp();
            }
        });
    }

    public void exitApp() {
        cefApp.dispose();
        dispose();
        System.exit(0); // stops Spring Boot
    }
}
