import 'dart:convert';
import 'dart:ui';

import 'package:flutter/material.dart';

import 'home_page_1.dart';
import 'home_page_2.dart';
import 'home_page_3.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        // This is the theme of your application.
        //
        // Try running your application with "flutter run". You'll see the
        // application has a blue toolbar. Then, without quitting the app, try
        // changing the primarySwatch below to Colors.green and then invoke
        // "hot reload" (press "r" in the console where you ran "flutter run",
        // or press Run > Flutter Hot Reload in a Flutter IDE). Notice that the
        // counter didn't reset back to zero; the application is not restarted.
        primarySwatch: Colors.blue,
      ),
      home: _widgetForRoute(window.defaultRouteName),
    );
  }

  Widget _widgetForRoute(String url) {
    String route = url.indexOf('?') == -1 ? url : url.substring(0, url.indexOf('?'));
    String paramsJson =
    url.indexOf('?') == -1 ? '{}' : url.substring(url.indexOf('?') + 1);
    // 解析参数
    Map<String, dynamic> params = json.decode(paramsJson);
    switch (route) {
      case 'route':
        return  MyHomePage(title: '第一页', desc: params.length > 0 ? params["desc"] : "",);
      case 'route2':
        return  MyHomePage2();
      case 'route3':
        return  MyHomePage3();
      default:
        return  MyHomePage(title: '第一页', desc: params.length > 0 ? params["desc"] : "",);

    }
  }
}