import { toPng } from 'html-to-image';
import { PLAY_STORE_URL } from '../../data/config';

export function incrementCardsSharedCount(): number {
  try {
    const current = parseInt(localStorage.getItem('cardsShared') || '0', 10);
    const updated = isNaN(current) ? 1 : current + 1;
    localStorage.setItem('cardsShared', updated.toString());
    return updated;
  } catch (e) {
    return 1;
  }
}

export function getCardsSharedCount(): number {
  try {
    return parseInt(localStorage.getItem('cardsShared') || '0', 10) || 0;
  } catch (e) {
    return 0;
  }
}

/**
 * Converts the off-screen ShareCard element to a PNG data URL and File.
 * Awaits document.fonts.ready so Noto Sans Telugu renders cleanly.
 */
export async function generatePanchangImage(elementId: string = 'panchang-share-card'): Promise<{ dataUrl: string; file: File }> {
  if (document.fonts) {
    await document.fonts.ready;
  }

  const node = document.getElementById(elementId);
  if (!node) {
    throw new Error(`Element with id ${elementId} not found`);
  }

  const dataUrl = await toPng(node, {
    width: 1080,
    height: 1350,
    pixelRatio: 1,
    cacheBust: true
  });

  const res = await fetch(dataUrl);
  const blob = await res.blob();
  const file = new File([blob], `Shubhodaya_Panchang_${Date.now()}.png`, { type: 'image/png' });

  return { dataUrl, file };
}

/**
 * Handles Web Share API, Capacitor, or fallback to download with WhatsApp toast.
 */
export async function sharePanchangImage(
  dataUrl: string,
  file: File,
  showToast: (msg: string) => void
): Promise<void> {
  const shareText = `Today's Panchang – Shubhodaya app: ${PLAY_STORE_URL}`;

  // 1. Try Web Share API with files
  if (navigator.canShare && navigator.canShare({ files: [file] })) {
    try {
      await navigator.share({
        files: [file],
        title: "Shubhodaya Panchangam",
        text: shareText
      });
      incrementCardsSharedCount();
      return;
    } catch (err: any) {
      if (err.name === 'AbortError') return; // user cancelled share
      console.warn('Web Share API failed, attempting Capacitor/Fallback', err);
    }
  }

  // 2. Check Capacitor Native Platform
  const win = window as any;
  if (win.Capacitor && win.Capacitor.isNativePlatform()) {
    try {
      const { Filesystem, Directory } = win.Capacitor.Plugins.Filesystem || {};
      const { Share } = win.Capacitor.Plugins.Share || {};

      if (Filesystem && Share) {
        const base64Data = dataUrl.split(',')[1];
        const fileName = `panchang_${Date.now()}.png`;

        const writeResult = await Filesystem.writeFile({
          path: fileName,
          data: base64Data,
          directory: Directory.Cache
        });

        await Share.share({
          title: "Shubhodaya Panchangam",
          text: shareText,
          url: writeResult.uri,
          dialogTitle: "Share Panchang"
        });

        incrementCardsSharedCount();
        return;
      }
    } catch (capErr) {
      console.warn('Capacitor share failed, falling back to download', capErr);
    }
  }

  // 3. Fallback: Download the PNG and prompt to share on WhatsApp
  try {
    const link = document.createElement('a');
    link.download = `Shubhodaya_Panchang_${new Date().toISOString().slice(0, 10)}.png`;
    link.href = dataUrl;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    incrementCardsSharedCount();
    showToast("Image saved – share it on WhatsApp");
  } catch (downloadErr) {
    console.error('Download fallback failed', downloadErr);
    showToast("Could not share image. Please try again.");
  }
}
