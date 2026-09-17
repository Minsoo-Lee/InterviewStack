import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiClient } from '../api/client';
import type { Question } from '../types';

// TODO: GET /api/questions 연동 (카테고리/세부주제/난이도 필터 UI는 워킹 스켈레톤 단계에서 추가)
export function QuestionListPage() {
  const [questions, setQuestions] = useState<Question[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiClient
      .get<Question[]>('/api/questions')
      .then((res) => setQuestions(res.data))
      .catch(() => setQuestions([]))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="page">
      <h1>질문은행</h1>

      {loading && <p>불러오는 중...</p>}

      {!loading && questions.length === 0 && (
        <p className="page__empty">표시할 질문이 없습니다.</p>
      )}

      <ul>
        {questions.map((q) => (
          <li key={q.id}>
            <Link to={`/questions/${q.id}`}>
              [{q.category} · {q.subTopic} · {q.difficulty}] {q.content}
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
