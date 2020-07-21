import 'package:animations/animations.dart';
import 'package:flutter/material.dart';
import 'package:flutter_module/second.dart';
import 'package:flutter_module/third.dart';
import 'package:provider/provider.dart';

import 'model.dart';

class MyHomePage2 extends StatefulWidget {
  MyHomePage2({Key key}) : super(key: key);

  @override
  _MyHomePageState2 createState() => _MyHomePageState2();
}

class _MyHomePageState2 extends State<MyHomePage2> {
  int pageIndex = 0;
  List<Widget> pageList = [
    SecondPage(title: "第一页", desc: "",),
    ThirdPage(title: "第二页", desc: "",)];

  final counter = CounterModel();
  final textSize = TextSizeModel();

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider<TextSizeModel>.value(
      value: textSize,
      child: ChangeNotifierProvider<CounterModel>.value(
        value: counter,
        child: Scaffold(
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
        ),
      ),
    );
  }
}