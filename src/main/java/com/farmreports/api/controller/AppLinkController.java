package com.farmreports.api.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open")
public class AppLinkController {

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String open() {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <title>Farm Reports</title>
                  <style>
                    * { box-sizing: border-box; margin: 0; padding: 0; }
                    body {
                      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
                      background: #f5f7f9;
                      display: flex;
                      align-items: center;
                      justify-content: center;
                      min-height: 100vh;
                      padding: 24px;
                    }
                    .card {
                      background: #fff;
                      border-radius: 16px;
                      padding: 40px 32px;
                      max-width: 400px;
                      width: 100%;
                      text-align: center;
                      box-shadow: 0 4px 24px rgba(0,0,0,0.08);
                    }
                    .icon {
                      width: 72px;
                      height: 72px;
                      background: #2d6a4f;
                      border-radius: 18px;
                      display: flex;
                      align-items: center;
                      justify-content: center;
                      margin: 0 auto 24px;
                      font-size: 36px;
                    }
                    h1 { font-size: 22px; color: #1a1a1a; margin-bottom: 8px; }
                    p  { font-size: 14px; color: #888; margin-bottom: 32px; line-height: 1.5; }
                    .btn {
                      display: block;
                      background: #2d6a4f;
                      color: #fff;
                      text-decoration: none;
                      font-size: 16px;
                      font-weight: 700;
                      padding: 16px;
                      border-radius: 12px;
                      margin-bottom: 16px;
                    }
                    .note { font-size: 12px; color: #aaa; }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <div class="icon">🌿</div>
                    <h1>Farm Reports</h1>
                    <p>Tap the button below to open the app.<br/>Make sure Expo Go is installed first.</p>
                    <a class="btn" href="exp://u.expo.dev/bdb6a038-448e-4bd1-977a-b5da6a662739?channel-name=preview">
                      Open Farm Reports
                    </a>
                    <p class="note">
                      Don't have Expo Go?<br/>
                      Search <strong>Expo Go</strong> on the App Store or Play Store.
                    </p>
                  </div>
                </body>
                </html>
                """;
    }
}
