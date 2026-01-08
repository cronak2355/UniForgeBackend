import { authService } from './authService';

const API_BASE_URL = 'https://uniforge.kr/api';

export interface LibraryItem {
    id: string;
    userId: string;
    itemType: 'GAME' | 'ASSET';
    refId: string;
    createdAt: string;
}

export const libraryService = {
    async addToLibrary(refId: string, itemType: 'GAME' | 'ASSET'): Promise<LibraryItem> {
        const token = authService.getToken();
        if (!token) throw new Error('로그인이 필요합니다.');

        const response = await fetch(`${API_BASE_URL}/library`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ refId, itemType })
        });

        if (!response.ok) {
            if (response.status === 400) throw new Error('이미 라이브러리에 존재하는 아이템입니다.');
            throw new Error('라이브러리 추가에 실패했습니다.');
        }

        return response.json();
    },

    async getLibrary(userId: string): Promise<LibraryItem[]> {
        const token = authService.getToken();
        const headers: HeadersInit = token ? { 'Authorization': `Bearer ${token}` } : {};

        const response = await fetch(`${API_BASE_URL}/library?userId=${userId}`, {
            headers
        });

        if (!response.ok) {
            throw new Error('라이브러리 조회에 실패했습니다.');
        }

        return response.json();
    }
};
