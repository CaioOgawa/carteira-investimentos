import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'screens/posicoes_list_screen.dart';

void main() {
  runApp(const ProviderScope(child: CarteiraApp()));
}

class CarteiraApp extends StatelessWidget {
  const CarteiraApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Carteira de Investimentos',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.indigo),
        useMaterial3: true,
      ),
      home: const PosicoesListScreen(),
    );
  }
}
