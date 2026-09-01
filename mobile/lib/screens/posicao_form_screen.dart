import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../models/posicao.dart';
import '../state/posicoes_provider.dart';

class PosicaoFormScreen extends ConsumerStatefulWidget {
  const PosicaoFormScreen({super.key});

  @override
  ConsumerState<PosicaoFormScreen> createState() => _PosicaoFormScreenState();
}

class _PosicaoFormScreenState extends ConsumerState<PosicaoFormScreen> {
  final _formKey = GlobalKey<FormState>();
  final _ativoController = TextEditingController();
  final _quantidadeController = TextEditingController();
  final _precoController = TextEditingController();
  DateTime _dataCompra = DateTime.now();
  bool _salvando = false;

  @override
  void dispose() {
    _ativoController.dispose();
    _quantidadeController.dispose();
    _precoController.dispose();
    super.dispose();
  }

  Future<void> _selecionarData() async {
    final selecionada = await showDatePicker(
      context: context,
      initialDate: _dataCompra,
      firstDate: DateTime(2000),
      lastDate: DateTime.now(),
    );
    if (selecionada != null) {
      setState(() => _dataCompra = selecionada);
    }
  }

  Future<void> _salvar() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _salvando = true);
    final posicao = Posicao(
      ativo: _ativoController.text.trim().toUpperCase(),
      quantidade: double.parse(_quantidadeController.text.replaceAll(',', '.')),
      precoCompra: double.parse(_precoController.text.replaceAll(',', '.')),
      dataCompra: _dataCompra,
    );

    try {
      await ref.read(posicoesProvider.notifier).adicionar(posicao);
      if (mounted) Navigator.of(context).pop();
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erro ao salvar: $e')),
        );
      }
    } finally {
      if (mounted) setState(() => _salvando = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Adicionar posição')),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            TextFormField(
              controller: _ativoController,
              textCapitalization: TextCapitalization.characters,
              decoration: const InputDecoration(
                labelText: 'Ativo (ex: PETR4)',
              ),
              validator: (valor) =>
                  (valor == null || valor.trim().isEmpty) ? 'Obrigatório' : null,
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _quantidadeController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: const InputDecoration(labelText: 'Quantidade'),
              validator: (valor) {
                final numero = double.tryParse((valor ?? '').replaceAll(',', '.'));
                if (numero == null || numero <= 0) return 'Quantidade inválida';
                return null;
              },
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _precoController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: const InputDecoration(labelText: 'Preço de compra'),
              validator: (valor) {
                final numero = double.tryParse((valor ?? '').replaceAll(',', '.'));
                if (numero == null || numero < 0) return 'Preço inválido';
                return null;
              },
            ),
            const SizedBox(height: 16),
            ListTile(
              contentPadding: EdgeInsets.zero,
              title: const Text('Data de compra'),
              subtitle: Text(
                '${_dataCompra.day.toString().padLeft(2, '0')}/'
                '${_dataCompra.month.toString().padLeft(2, '0')}/'
                '${_dataCompra.year}',
              ),
              trailing: const Icon(Icons.calendar_today),
              onTap: _selecionarData,
            ),
            const SizedBox(height: 24),
            FilledButton(
              onPressed: _salvando ? null : _salvar,
              child: _salvando
                  ? const SizedBox(
                      width: 20,
                      height: 20,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    )
                  : const Text('Salvar'),
            ),
          ],
        ),
      ),
    );
  }
}
