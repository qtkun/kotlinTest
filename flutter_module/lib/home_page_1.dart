import 'package:animations/animations.dart';
import 'package:flutter/material.dart';

import 'IntentPlugin.dart';
import 'ToastPlugin.dart';
import 'fourth.dart';

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
          return FourthPage(title: "第二页", desc: widget.desc ?? "", hasPrevious: true,);
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