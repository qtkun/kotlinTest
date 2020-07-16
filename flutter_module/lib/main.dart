import 'dart:convert';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_module/IntentPlugin.dart';
import 'package:flutter_module/ToastPlugin.dart';

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
      default:
        return  MyHomePage(title: '第一页');

    }
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title, this.desc}) : super(key: key);

  final String title;
  final String desc;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
        leading:IconButton(
          icon: const Icon(Icons.arrow_back_ios),
          onPressed: () {
            IntentPlugin.goBack();
          },
          tooltip: MaterialLocalizations.of(context).openAppDrawerTooltip,
        ),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              "点击跳转下一页"
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          Navigator.push(context, MaterialPageRoute(builder: (context) => SecondPage(title: "第二页", desc: widget.desc,)));
//          IntentPlugin.jumpToSetting();
//          IntentPlugin.goBack();
//          SystemNavigator.pop()
        },
        tooltip: 'Increment',
        child: Icon(Icons.arrow_forward),
      ),
    );
  }
}

class SecondPage extends StatefulWidget {
  SecondPage({Key key, this.title, this.desc}) : super(key: key);

  final String title;
  final String desc;

  @override
  _SecondPageState createState() => _SecondPageState();
}

class _SecondPageState extends State<SecondPage> {
  int _counter = 0;

  void _incrementCounter() {
    setState(() {
      _counter++;
    });
  }

  @override
  void initState() {
    IntentPlugin.channel.setMethodCallHandler((call) => handler(call));
    super.initState();
  }

  Future<dynamic> handler(MethodCall call) async {
    switch (call.method) {
      case 'onActivityResult':
        ToastPlugin.toast(call.arguments["toast"]);
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
        leading:IconButton(
          icon: const Icon(Icons.arrow_back_ios),
          onPressed: () {
            Navigator.pop(context);
          },
          tooltip: MaterialLocalizations.of(context).openAppDrawerTooltip,
        ),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              widget.desc == "" ? 'You have pushed the button this many times:' : widget.desc,
            ),
            Text(
              '$_counter',
              style: Theme.of(context).textTheme.headline4,
            ),
            FlatButton.icon(
              onPressed: _incrementCounter,
              label: Text("1", style: TextStyle(color: Colors.white, fontSize: 20),),
              icon: Icon(Icons.add, color: Colors.white,),
              color: Theme.of(context).primaryColor,
            )
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          IntentPlugin.jumpToSettingForResult();
        },
        child: Icon(Icons.arrow_forward),
      ),
    );
  }
}