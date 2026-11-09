import { toPng } from 'html-to-image';
import { PLAY_STORE_URL } from '../../data/config';

export interface SavedGreetingCreation {
  id: string;
  templateId: string;
  templateTitle: string;
  category: string;
  dataUrl: string;
  createdAt: number;
  userName?: string;
}

const DB_NAME = 'shubhodaya_greetings_db';
const STORE_NAME = 'my_creations';
const DB_VERSION = 1;
const MAX_SAVED_CREATIONS = 20;

function openCreationsDB(): Promise<IDBDatabase> {
  return new Promise((resolve, reject) => {
    if (!window.indexedDB) {
      reject(new Error('IndexedDB not supported'));
      return;
    }
    const request = indexedDB.open(DB_NAME, DB_VERSION);
    request.onupgradeneeded = (event: any) => {
      const db = event.target.result;
      if (!db.objectStoreNames.contains(STORE_NAME)) {
        db.createObjectStore(STORE_NAME, { keyPath: 'id' });
      }
    };
    request.onsuccess = () => resolve(request.result);
    request.onerror = () => reject(request.error);
  });
}

/**
 * Saves a generated greeting creation into IndexedDB (keeps last 20).
 */
export async function saveCreationToIndexedDB(creation: SavedGreetingCreation): Promise<void> {
  try {
    const db = await openCreationsDB();
    const tx = db.transaction(STORE_NAME, 'readwrite');
    const store = tx.objectStore(STORE_NAME);

    // Get all existing items
    const allItems: SavedGreetingCreation[] = await new Promise((res, rej) => {
      const req = store.getAll();
      req.onsuccess = () => res(req.result || []);
      req.onerror = () => rej(req.error);
    });

    // Add new item
    store.put(creation);

    // If over 20, delete oldest
    allItems.push(creation);
    allItems.sort((a, b) => b.createdAt - a.createdAt);

    if (allItems.length > MAX_SAVED_CREATIONS) {
      const toDelete = allItems.slice(MAX_SAVED_CREATIONS);
      for (const item of toDelete) {
        store.delete(item.id);
      }
    }
  } catch (err) {
    console.warn('Could not save greeting creation to IndexedDB:', err);
  }
}

/**
 * Retrieves the saved creations from IndexedDB, sorted newest first.
 */
export async function getCreationsFromIndexedDB(): Promise<SavedGreetingCreation[]> {
  try {
    const db = await openCreationsDB();
    const tx = db.transaction(STORE_NAME, 'readonly');
    const store = tx.objectStore(STORE_NAME);
    const items: SavedGreetingCreation[] = await new Promise((res, rej) => {
      const req = store.getAll();
      req.onsuccess = () => res(req.result || []);
      req.onerror = () => rej(req.error);
    });
    return items.sort((a, b) => b.createdAt - a.createdAt);
  } catch (err) {
    console.warn('Could not load creations from IndexedDB:', err);
    return [];
  }
}

/**
 * Converts a 1080x1080 greeting DOM node to PNG.
 * Awaits document.fonts.ready so Noto Sans Telugu and Poppins render properly.
 */
export async function generateGreetingImage(
  elementId: string = 'greeting-card-canvas'
): Promise<{ dataUrl: string; file: File }> {
  if (document.fonts) {
    await document.fonts.ready;
  }

  const node = document.getElementById(elementId);
  if (!node) {
    throw new Error(`Element #${elementId} not found`);
  }

  const dataUrl = await toPng(node, {
    width: 1080,
    height: 1080,
    pixelRatio: 1,
    cacheBust: true
  });

  const res = await fetch(dataUrl);
  const blob = await res.blob();
  const file = new File([blob], `Shubhodaya_Greeting_${Date.now()}.png`, { type: 'image/png' });

  return { dataUrl, file };
}

/**
 * Dispatches sharing of the greeting image via Web Share API, Capacitor, or download fallback.
 */
export async function shareGreetingImage(
  dataUrl: string,
  file: File,
  showToast: (msg: string) => void
): Promise<void> {
  const shareText = `Make your own greetings free: ${PLAY_STORE_URL}`;

  // 1. Web Share API with files
  if (navigator.canShare && navigator.canShare({ files: [file] })) {
    try {
      await navigator.share({
        files: [file],
        title: "Shubhodaya Greetings",
        text: shareText
      });
      return;
    } catch (err: any) {
      if (err.name === 'AbortError') return;
      console.warn('Web Share failed, attempting Capacitor/Fallback', err);
    }
  }

  // 2. Capacitor native share
  const win = window as any;
  if (win.Capacitor && win.Capacitor.isNativePlatform()) {
    try {
      const { Filesystem, Directory } = win.Capacitor.Plugins.Filesystem || {};
      const { Share } = win.Capacitor.Plugins.Share || {};
      if (Filesystem && Share) {
        const base64Data = dataUrl.split(',')[1];
        const fileName = `greeting_${Date.now()}.png`;

        const writeRes = await Filesystem.writeFile({
          path: fileName,
          data: base64Data,
          directory: Directory.Cache
        });

        await Share.share({
          title: "Shubhodaya Greetings",
          text: shareText,
          url: writeRes.uri,
          dialogTitle: "Share Greeting"
        });
        return;
      }
    } catch (capErr) {
      console.warn('Capacitor greeting share failed, falling back to download', capErr);
    }
  }

  // 3. Download Fallback
  try {
    const link = document.createElement('a');
    link.download = `Shubhodaya_Greeting_${Date.now()}.png`;
    link.href = dataUrl;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    showToast("Image saved – share it on WhatsApp");
  } catch (e) {
    showToast("Could not share image. Please try again.");
  }
}
