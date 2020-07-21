import 'package:flutter/material.dart';

class CounterModel with ChangeNotifier{
  int _count = 12;
  get value => _count;

  void increment(){
    _count++;
    notifyListeners();
  }
}

class TextSizeModel with ChangeNotifier{
  int _size = 12;
  get value => _size;

  void increment(){
    _size++;
    notifyListeners();
  }
}