import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';

import '../state/auth_provider.dart';
import '../state/posicoes_provider.dart';
import 'posicao_detail_screen.dart';
import 'posicao_form_screen.dart';

class PosicoesListScreen extends ConsumerWidget {
  const PosicoesListScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final posicoesAsync = ref.watch(posicoesProvider);
    final moeda = NumberFormat.simpleCurrency(locale: 'pt_BR');

    return Scaffold(
      appBar: AppBar(
        title: const Text('Minha carteira'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            tooltip: 'Sair',
            onPressed: () => ref.read(authControllerProvider).logout(),
          ),
        ],
      ),
      body: posicoesAsync.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (erro, _) => Center(
          child: Padding(
            padding: const EdgeInsets.all(24),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const Icon(Icons.cloud_off, size: 48, color: Colors.grey),
                const SizedBox(height: 12),
                Text(
                  'Não foi possível carregar as posições.\n$erro',
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 12),
                FilledButton(
                  onPressed: () => ref.invalidate(posicoesProvider),
                  child: const Text('Tentar novamente'),
                ),
              ],
            ),
          ),
        ),
        data: (posicoes) {
          if (posicoes.isEmpty) {
            return const Center(child: Text('Nenhuma posição cadastrada ainda.'));
          }
          return RefreshIndicator(
            onRefresh: () => ref.refresh(posicoesProvider.future),
            child: ListView.separated(
              itemCount: posicoes.length,
              separatorBuilder: (_, _) => const Divider(height: 1),
              itemBuilder: (context, index) {
                final posicao = posicoes[index];
                return ListTile(
                  title: Text(posicao.ativo),
                  subtitle: Text('${posicao.quantidade} un.'),
                  trailing: Text(moeda.format(posicao.precoCompra)),
                  onTap: () {
                    Navigator.of(context).push(
                      MaterialPageRoute(
                        builder: (_) => PosicaoDetailScreen(posicao: posicao),
                      ),
                    );
                  },
                );
              },
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          Navigator.of(context).push(
            MaterialPageRoute(builder: (_) => const PosicaoFormScreen()),
          );
        },
        child: const Icon(Icons.add),
      ),
    );
  }
}
