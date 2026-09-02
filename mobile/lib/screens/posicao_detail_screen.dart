import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';

import '../models/posicao.dart';
import '../state/posicoes_provider.dart';

class PosicaoDetailScreen extends ConsumerWidget {
  const PosicaoDetailScreen({super.key, required this.posicao});

  final Posicao posicao;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final moeda = NumberFormat.simpleCurrency(locale: 'pt_BR');
    final valorTotal = posicao.quantidade * posicao.precoCompra;

    return Scaffold(
      appBar: AppBar(
        title: Text(posicao.ativo),
        actions: [
          IconButton(
            icon: const Icon(Icons.delete_outline),
            tooltip: 'Remover',
            onPressed: () async {
              await ref.read(posicoesProvider.notifier).remover(posicao.id!);
              if (context.mounted) Navigator.of(context).pop();
            },
          ),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _linha('Ativo', posicao.ativo),
            _linha('Quantidade', posicao.quantidade.toString()),
            _linha('Preço de compra', moeda.format(posicao.precoCompra)),
            _linha('Valor total', moeda.format(valorTotal)),
            _linha(
              'Data de compra',
              DateFormat('dd/MM/yyyy').format(posicao.dataCompra),
            ),
          ],
        ),
      ),
    );
  }

  Widget _linha(String rotulo, String valor) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(rotulo, style: const TextStyle(color: Colors.grey)),
          Text(valor, style: const TextStyle(fontWeight: FontWeight.bold)),
        ],
      ),
    );
  }
}
