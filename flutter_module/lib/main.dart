import 'dart:convert';
import 'dart:ui';

import 'package:animations/animations.dart';
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
      case 'route2':
        return  MyHomePage2();
      case 'route3':
        return  MyHomePage3();
      default:
        return  MyHomePage(title: '第一页', desc: params.length > 0 ? params["desc"] : "",);

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
      floatingActionButton: OpenContainer(
        openBuilder: (context, CloseContainerActionCallback<List> action) {
          return SecondPage(title: "第二页", desc: widget.desc ?? "",);
//          IntentPlugin.jumpToSetting();
//          IntentPlugin.goBack();
//          SystemNavigator.pop()
        },
        onClosed: (data) {
          if (data is List) {
            ToastPlugin.toast(data[0]);
          }
        },
        closedElevation: 6.0,
        closedShape: const RoundedRectangleBorder(
          borderRadius: BorderRadius.all(
            Radius.circular(50),
          ),
        ),
        closedColor: Theme.of(context).colorScheme.secondary,
        closedBuilder: (context, action) {
          return SizedBox(
            height: 50,
            width: 50,
            child: Center(
              child: Icon(
                Icons.arrow_forward,
                color: Theme.of(context).colorScheme.onSecondary,
              ),
            ),
          );
        },
      ),
    );
  }
}

class MyHomePage2 extends StatefulWidget {
  MyHomePage2({Key key}) : super(key: key);

  @override
  _MyHomePageState2 createState() => _MyHomePageState2();
}

class _MyHomePageState2 extends State<MyHomePage2> {
  int pageIndex = 0;
  List<Widget> pageList = [
    SecondPage(title: "第一页", desc: "点击1", color: Colors.amberAccent,),
    ThirdPage(title: "第二页", desc: "点击2", color: Colors.redAccent,)];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: PageTransitionSwitcher(
        transitionBuilder: (
            Widget child,
            Animation<double> animation,
            Animation<double> secondaryAnimation,
            ) {
          return FadeThroughTransition(
            animation: animation,
            secondaryAnimation: secondaryAnimation,
            child: child,
          );
        },
        child: pageList[pageIndex],
      ),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: pageIndex,
        onTap: (int newValue) {
          setState(() {
            pageIndex = newValue;
          });
        },
        items: const <BottomNavigationBarItem>[
          BottomNavigationBarItem(
            icon: Icon(Icons.photo_library),
            title: Text('Albums'),
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.photo),
            title: Text('Photos'),
          ),
        ],
      ),
    );
  }
}

class MyHomePage3 extends StatefulWidget {
  MyHomePage3({Key key}) : super(key: key);

  @override
  _MyHomePageState3 createState() => _MyHomePageState3();
}

class _MyHomePageState3 extends State<MyHomePage3> {
  int pageIndex = 0;
  List<Widget> pageList = [
    SecondPage(title: "第一页", desc: "点击1", color: Colors.amberAccent,),
    ThirdPage(title: "第二页", desc: "点击2", color: Colors.redAccent,)];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: PageTransitionSwitcher(
        transitionBuilder: (
            Widget child,
            Animation<double> animation,
            Animation<double> secondaryAnimation,
            ) {
          return SharedAxisTransition(
            child: child,
            animation: animation,
            transitionType: SharedAxisTransitionType.scaled,
            secondaryAnimation: secondaryAnimation,
          );
        },
        child: pageList[pageIndex],
      ),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: pageIndex,
        onTap: (int newValue) {
          setState(() {
            pageIndex = newValue;
          });
        },
        items: const [
          BottomNavigationBarItem(
              title: Text('首页'),
              icon: Icon(Icons.home)
          ),
          BottomNavigationBarItem(
              title: Text('我的'),
              icon: Icon(Icons.perm_identity)
          ),
        ],
      ),
    );
  }
}

class SecondPage extends StatefulWidget {
  SecondPage({Key key, this.title, this.desc, this.color}) : super(key: key);

  final String title;
  final String desc;
  final Color color;

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
            Navigator.of(context).pop(["返回第一页", 1]);
          },
          tooltip: MaterialLocalizations.of(context).openAppDrawerTooltip,
        ),
      ),
      body: Container(
        color: widget.color ?? Colors.white,
        child: Center(
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

class ThirdPage extends StatefulWidget {
  ThirdPage({Key key, this.title, this.desc, this.color}) : super(key: key);

  final String title;
  final String desc;
  final Color color;

  @override
  _ThirdPageState createState() => _ThirdPageState();
}

class _ThirdPageState extends State<ThirdPage> {
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
            Navigator.of(context).pop(["返回第一页", 1]);
          },
          tooltip: MaterialLocalizations.of(context).openAppDrawerTooltip,
        ),
      ),
      body: Container(
        color: widget.color ?? Colors.white,
        child: Center(
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