import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'IntentPlugin.dart';
import 'ToastPlugin.dart';

class FourthPage extends StatefulWidget {
  FourthPage({Key key, this.title, this.desc, this.color, this.hasPrevious = false}) : super(key: key);

  final String title;
  final String desc;
  final Color color;
  final bool hasPrevious;

  @override
  _FourthPageState createState() => _FourthPageState();
}

class _FourthPageState extends State<FourthPage> {
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
            if (widget.hasPrevious) {
              Navigator.of(context).pop(["返回第一页", 1]);
            } else {
              IntentPlugin.goBack();
            }
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
                widget.desc == "" ? '你已经点击这么多次:' : widget.desc,
              ),
              Text(
                '$_counter',
                style: Theme.of(context).textTheme.headline4,
              ),
              TextButton.icon(
                onPressed: _incrementCounter,
                label: Text("1", style: TextStyle(color: Colors.white, fontSize: 20),),
                icon: Icon(Icons.add, color: Colors.white,),
                style: TextButton.styleFrom(
                  primary: Theme.of(context).primaryColor,
                ),
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