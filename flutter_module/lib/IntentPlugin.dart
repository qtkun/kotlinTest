import 'package:flutter/services.dart';

class IntentPlugin{
  static const MethodChannel channel = MethodChannel('intent_plugin');

  static Future<String> jumpToSetting() async {
    Map<String, String> params = {'toast': "跳转成功"};
    return await channel.invokeMethod('jumpToSetting', params);
  }

  static Future<String> goBack() async {
    Map<String, String> params = {'toast': "我从Flutter页面回来了"};
    return await channel.invokeMethod('goBackWithResult', params);
  }

  static Future<String> jumpToSettingForResult() async {
    Map<String, dynamic> params = {
      'REQUEST_CODE' : 0x02,
      'toast': "跳转成功"
    };
    return await channel.invokeMethod('jumpToSettingForResult', params);
  }
}