import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';

import 'IntentPlugin.dart';
import 'ToastPlugin.dart';
import 'model.dart';

class SecondPage extends StatefulWidget {
  SecondPage({Key key, this.title, this.desc, this.color, this.hasPrevious = false}) : super(key: key);

  final String title;
  final String desc;
  final Color color;
  final bool hasPrevious;

  @override
  _SecondPageState createState() => _SecondPageState();
}

class _SecondPageState extends State<SecondPage> {

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
//    final _counter = Provider.of<CounterModel>(context);
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
      body: Consumer2<CounterModel, TextSizeModel>(builder: (context, counter, textSize,_) {
        return Container(
          color: widget.color ?? Colors.white,
          child: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                Text(
                  widget.desc == "" ? '我现在这么大:' : widget.desc,
                  style: TextStyle(
                      fontSize: textSize.value.toDouble()
                  ),
                ),
                Text(
                  '${counter.value}',
                  style: Theme.of(context).textTheme.headline4,
                ),
                FlatButton.icon(
                  onPressed: () {
                    counter.increment();
                    textSize.increment();
                  },
                  label: Text("1", style: TextStyle(color: Colors.white, fontSize: 20),),
                  icon: Icon(Icons.add, color: Colors.white,),
                  color: Theme.of(context).primaryColor,
                )
              ],
            ),
          ),
        );
      }),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          IntentPlugin.jumpToSettingForResult();
        },
        child: Icon(Icons.arrow_forward),
      ),
    );
  }
}