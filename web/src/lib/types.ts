export interface UserProfile {
  id: number;
  lineSub: string;
  displayName?: string | null;
  pictureUrl?: string | null;
}

export interface AuthResponse {
  token: string;
  user: UserProfile;
}

export interface TodayTasks {
  expiringSoon: ExpiringItem[];
  expired: ExpiredItem[];
  lowStock: LowStockItem[];
  suggestedConsume: SuggestedConsume[];
}

export interface ExpiringItem {
  name: string;
  expiresOn: string;
}

export interface ExpiredItem {
  name: string;
  expiredOn: string;
}

export interface LowStockItem {
  category: string;
  suggestion: string;
}

export interface SuggestedConsume {
  name: string;
  reason: string;
}

export interface ProductSummary {
  id: number;
  barcode: string;
  name: string;
  brand?: string | null;
  category?: string | null;
}

export interface ScanResponse {
  productCandidate: ProductSummary | null;
  lastUsedExpiryTemplates: string[];
}

export interface StockLotView {
  id: number;
  expiresOn: string;
  quantity: number;
  purchasedOn?: string | null;
}

export interface StockItemView {
  id: number;
  product: ProductSummary;
  unit: string;
  totalQuantity: number;
  nextLot?: StockLotView | null;
}

export interface RegisterStockRequest {
  barcode: string;
  name: string;
  brand?: string;
  category?: string;
  quantity: number;
  unit: string;
  expiresOn: string;
  purchasedOn?: string;
}

export interface ConsumptionResult {
  consumedLots: StockLotView[];
  remainingTotal: number;
}

export interface ConsumeRequest {
  stockItemId: number;
  quantity: number;
  reason?: string;
}
