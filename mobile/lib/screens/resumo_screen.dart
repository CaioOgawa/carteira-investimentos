import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';

import '../models/resumo_carteira.dart';
import '../state/resumo_provider.dart';

const _paletaCores = [
  Colors.indigo,
  Colors.teal,
  Colors.orange,
  Colors.purple,
  Colors.pink,
  Colors.blueGrey,
  Colors.amber,
  Colors.cyan,
];

class ResumoScreen extends ConsumerWidget {
  const ResumoScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final resumoAsync = ref.watch(resumoProvider);
    final moeda = NumberFormat.simpleCurrency(locale: 'pt_BR');

    return Scaffold(
      appBar: AppBar(title: const Text('Resumo da carteira')),
      body: resumoAsync.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (erro, _) => Center(
          child: Padding(
            padding: const EdgeInsets.all(24),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const Icon(Icons.cloud_off, size: 48, color: Colors.grey),
                const SizedBox(height: 12),
                Text('Não foi possível carregar o resumo.\n$erro', textAlign: TextAlign.center),
                const SizedBox(height: 12),
                FilledButton(
                  onPressed: () => ref.invalidate(resumoProvider),
                  child: const Text('Tentar novamente'),
                ),
              ],
            ),
          ),
        ),
        data: (resumo) {
          if (resumo.ativos.isEmpty) {
            return const Center(child: Text('Nenhuma posição para resumir ainda.'));
          }
          return RefreshIndicator(
            onRefresh: () => ref.refresh(resumoProvider.future),
            child: ListView(
              padding: const EdgeInsets.all(16),
              children: [
                _cartoesResumo(context, resumo, moeda),
                const SizedBox(height: 24),
                Text('Composição por ativo', style: Theme.of(context).textTheme.titleMedium),
                const SizedBox(height: 12),
                _graficoComposicao(resumo),
                const SizedBox(height: 24),
                Text('Ganho/perda por ativo (%)', style: Theme.of(context).textTheme.titleMedium),
                const SizedBox(height: 12),
                _graficoGanhoPerda(resumo),
              ],
            ),
          );
        },
      ),
    );
  }

  Widget _cartoesResumo(BuildContext context, ResumoCarteira resumo, NumberFormat moeda) {
    final ganho = resumo.ganhoPerdaTotal >= 0;
    final corGanho = ganho ? Colors.green : Colors.red;

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _linhaResumo('Valor investido', moeda.format(resumo.valorTotalInvestido)),
            const SizedBox(height: 8),
            _linhaResumo('Valor atual', moeda.format(resumo.valorTotalAtual)),
            const Divider(height: 24),
            _linhaResumo(
              'Ganho/perda',
              '${moeda.format(resumo.ganhoPerdaTotal)}  (${ganho ? '+' : ''}${resumo.percentualTotal.toStringAsFixed(2)}%)',
              cor: corGanho,
            ),
          ],
        ),
      ),
    );
  }

  Widget _linhaResumo(String rotulo, String valor, {Color? cor}) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(rotulo, style: const TextStyle(color: Colors.grey)),
        Text(valor, style: TextStyle(fontWeight: FontWeight.bold, color: cor)),
      ],
    );
  }

  Widget _graficoComposicao(ResumoCarteira resumo) {
    return Row(
      children: [
        SizedBox(
          height: 160,
          width: 160,
          child: PieChart(
            PieChartData(
              sectionsSpace: 2,
              centerSpaceRadius: 32,
              sections: [
                for (var i = 0; i < resumo.ativos.length; i++)
                  PieChartSectionData(
                    value: resumo.ativos[i].valorAtual,
                    color: _paletaCores[i % _paletaCores.length],
                    title: '',
                    radius: 48,
                  ),
              ],
            ),
          ),
        ),
        const SizedBox(width: 16),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              for (var i = 0; i < resumo.ativos.length; i++)
                Padding(
                  padding: const EdgeInsets.symmetric(vertical: 2),
                  child: Row(
                    children: [
                      Container(
                        width: 10,
                        height: 10,
                        decoration: BoxDecoration(
                          color: _paletaCores[i % _paletaCores.length],
                          shape: BoxShape.circle,
                        ),
                      ),
                      const SizedBox(width: 8),
                      Expanded(
                        child: Text(
                          resumo.ativos[i].ativo,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                      Text(
                        '${(resumo.ativos[i].valorAtual / resumo.valorTotalAtual * 100).toStringAsFixed(1)}%',
                        style: const TextStyle(color: Colors.grey),
                      ),
                    ],
                  ),
                ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _graficoGanhoPerda(ResumoCarteira resumo) {
    final maiorAbsoluto = resumo.ativos
        .map((a) => a.percentual.abs())
        .fold<double>(1, (max, v) => v > max ? v : max);
    final teto = (maiorAbsoluto * 1.2).clamp(1.0, double.infinity);

    return SizedBox(
      height: 220,
      child: BarChart(
        BarChartData(
          minY: -teto,
          maxY: teto,
          gridData: const FlGridData(drawVerticalLine: false),
          borderData: FlBorderData(show: false),
          titlesData: FlTitlesData(
            topTitles: const AxisTitles(sideTitles: SideTitles(showTitles: false)),
            rightTitles: const AxisTitles(sideTitles: SideTitles(showTitles: false)),
            leftTitles: const AxisTitles(sideTitles: SideTitles(showTitles: true, reservedSize: 40)),
            bottomTitles: AxisTitles(
              sideTitles: SideTitles(
                showTitles: true,
                getTitlesWidget: (value, meta) {
                  final indice = value.toInt();
                  if (indice < 0 || indice >= resumo.ativos.length) return const SizedBox.shrink();
                  return Padding(
                    padding: const EdgeInsets.only(top: 4),
                    child: Text(resumo.ativos[indice].ativo, style: const TextStyle(fontSize: 11)),
                  );
                },
              ),
            ),
          ),
          barGroups: [
            for (var i = 0; i < resumo.ativos.length; i++)
              BarChartGroupData(
                x: i,
                barRods: [
                  BarChartRodData(
                    toY: resumo.ativos[i].percentual,
                    color: resumo.ativos[i].percentual >= 0 ? Colors.green : Colors.red,
                    width: 18,
                    borderRadius: BorderRadius.circular(4),
                  ),
                ],
              ),
          ],
        ),
      ),
    );
  }
}
