import 'package:animations/animations.dart';
import 'package:flutter/material.dart';
import 'package:flutter_module/second.dart';
import 'package:flutter_module/third.dart';
import 'package:provider/provider.dart';

import 'model.dart';

class MyHomePage3 extends StatefulWidget {
  MyHomePage3({Key key}) : super(key: key);

  @override
  _MyHomePageState3 createState() => _MyHomePageState3();
}

class _MyHomePageState3 extends State<MyHomePage3> {
  int pageIndex = 0;
  List<Widget> pageList = [
    SecondPage(title: "第一页", desc: "", color: Colors.amberAccent,),
    ThirdPage(title: "第二页", desc: "", color: Colors.redAccent,)];

  final counter = CounterModel();
  final textSize = TextSizeModel();

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider.value(value: textSize),
        ChangeNotifierProvider.value(value: counter)
      ],
      child: Scaffold(
        body: PageTransitionSwitcher(
          transitionBuilder: (
              Widget child,
              Animation<double> animation,
              Animation<double> secondaryAnimation,
              ) {
            return SharedAxisTransition(
              child: child,
              animation: animation,
              transitionType: SharedAxisTransitionType.vertical,
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
      ),
    );
  }
}