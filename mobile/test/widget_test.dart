import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:carteira_mobile/main.dart';
import 'package:carteira_mobile/models/posicao.dart';
import 'package:carteira_mobile/services/posicao_api.dart';
import 'package:carteira_mobile/state/posicoes_provider.dart';

class _FakePosicaoApi extends PosicaoApi {
  final List<Posicao> _posicoes;
  _FakePosicaoApi(this._posicoes) : super();

  @override
  Future<List<Posicao>> listar() async => _posicoes;
}

void main() {
  testWidgets('mostra a lista de posições vinda da API', (tester) async {
    final posicao = Posicao(
      id: 1,
      ativo: 'PETR4',
      quantidade: 100,
      precoCompra: 32.5,
      dataCompra: DateTime(2026, 1, 15),
    );

    await tester.pumpWidget(
      ProviderScope(
        overrides: [
          posicaoApiProvider.overrideWithValue(_FakePosicaoApi([posicao])),
        ],
        child: const CarteiraApp(),
      ),
    );

    await tester.pumpAndSettle();

    expect(find.text('Minha carteira'), findsOneWidget);
    expect(find.text('PETR4'), findsOneWidget);
  });

  testWidgets('mostra estado vazio quando não há posições', (tester) async {
    await tester.pumpWidget(
      ProviderScope(
        overrides: [
          posicaoApiProvider.overrideWithValue(_FakePosicaoApi([])),
        ],
        child: const CarteiraApp(),
      ),
    );

    await tester.pumpAndSettle();

    expect(find.text('Nenhuma posição cadastrada ainda.'), findsOneWidget);
  });
}
