import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../models/resumo_carteira.dart';
import '../services/resumo_api.dart';
import 'auth_provider.dart';

final resumoApiProvider = Provider<ResumoApi>((ref) => ResumoApi(ref.watch(dioProvider)));

final resumoProvider = FutureProvider.autoDispose<ResumoCarteira>((ref) {
  return ref.watch(resumoApiProvider).buscar();
});
