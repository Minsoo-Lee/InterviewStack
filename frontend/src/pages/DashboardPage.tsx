import { useEffect, useState } from 'react';
import { apiClient } from '../api/client';
import type { DashboardSummary } from '../types';

// TODO: GET /api/dashboard 연동 (현재는 스켈레톤 - 워킹 스켈레톤 단계에서 실데이터 연결)
export function DashboardPage() {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiClient
      .get<DashboardSummary>('/api/dashboard')
      .then((res) => setSummary(res.data))
      .catch(() => setSummary(null))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="page">
      <h1>대시보드</h1>
      <p className="page__hint">취약 카테고리와 오답노트를 한눈에 확인하세요.</p>

      {loading && <p>불러오는 중...</p>}

      {!loading && !summary && (
        <p className="page__empty">아직 표시할 데이터가 없습니다. 질문은행에서 첫 답변을 제출해보세요.</p>
      )}

      {summary && (
        <>
          <section>
            <h2>취약 카테고리</h2>
            <ul>
              {summary.weakCategories.map((c) => (
                <li key={c.category}>
                  {c.category} · 평균 {c.averageScore.toFixed(1)}점 ({c.answeredCount}문항)
                </li>
              ))}
            </ul>
          </section>

          <section>
            <h2>오답노트</h2>
            <ul>
              {summary.wrongAnswerNotes.map((note) => (
                <li key={note.answer.id}>{note.feedback.summary}</li>
              ))}
            </ul>
          </section>
        </>
      )}
    </div>
  );
}
