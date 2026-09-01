import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../models/posicao.dart';
import '../services/posicao_api.dart';

final posicaoApiProvider = Provider<PosicaoApi>((ref) => PosicaoApi());

class PosicoesNotifier extends AsyncNotifier<List<Posicao>> {
  @override
  Future<List<Posicao>> build() {
    return ref.read(posicaoApiProvider).listar();
  }

  Future<void> adicionar(Posicao posicao) async {
    await ref.read(posicaoApiProvider).criar(posicao);
    ref.invalidateSelf();
    await future;
  }

  Future<void> remover(int id) async {
    await ref.read(posicaoApiProvider).remover(id);
    ref.invalidateSelf();
    await future;
  }
}

final posicoesProvider =
    AsyncNotifierProvider<PosicoesNotifier, List<Posicao>>(PosicoesNotifier.new);
