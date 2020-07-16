import 'package:flutter/services.dart';

class ToastPlugin {
  static const MethodChannel channel = MethodChannel('toast_plugin');

  static Future<String> toast(String message) async {
    Map<String, String> params = {'message': message};
    return await channel.invokeMethod('toast', params);
  }
}