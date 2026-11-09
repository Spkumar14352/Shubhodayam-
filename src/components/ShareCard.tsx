import React from 'react';
import { PLAY_STORE_URL } from '../../data/config';
import { getDailyQuote } from '../../data/dailyQuotes';

export interface ShareCardProps {
  dateStrTe: string;
  dateStrEn: string;
  vaaramTe: string;
  vaaramEn: string;
  tithiTe: string;
  tithiEn: string;
  nakshatraTe: string;
  nakshatraEn: string;
  pakshaTe: string;
  pakshaEn: string;
  sunrise: string;
  sunset: string;
  rahuKalam: string;
  yamagandam: string;
  abhijitMuhurtham: string;
  festivalNameTe?: string;
  festivalNameEn?: string;
  isTelugu?: boolean;
  dayOfYear?: number;
}

export const ShareCard: React.FC<ShareCardProps> = ({
  dateStrTe,
  dateStrEn,
  vaaramTe,
  vaaramEn,
  tithiTe,
  tithiEn,
  nakshatraTe,
  nakshatraEn,
  pakshaTe,
  pakshaEn,
  sunrise,
  sunset,
  rahuKalam,
  yamagandam,
  abhijitMuhurtham,
  festivalNameTe,
  festivalNameEn,
  isTelugu = true,
  dayOfYear
}) => {
  const quote = getDailyQuote(dayOfYear);
  const festival = isTelugu ? festivalNameTe : festivalNameEn;

  return (
    <div
      id="panchang-share-card"
      style={{
        width: 1080,
        height: 1350,
        backgroundColor: '#5B0E1E',
        color: '#FFF7EC',
        fontFamily: isTelugu ? "'Noto Sans Telugu', 'Poppins', sans-serif" : "'Poppins', sans-serif",
        position: 'relative',
        boxSizing: 'border-box',
        padding: '48px 56px',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        overflow: 'hidden'
      }}
    >
      {/* Background Decorative Mandala Pattern Overlay */}
      <svg
        style={{
          position: 'absolute',
          top: -120,
          right: -120,
          width: 500,
          height: 500,
          opacity: 0.08,
          pointerEvents: 'none'
        }}
        viewBox="0 0 100 100"
      >
        <circle cx="50" cy="50" r="45" fill="none" stroke="#D4A537" strokeWidth="1.5" />
        <circle cx="50" cy="50" r="35" fill="none" stroke="#D4A537" strokeWidth="1" />
        <circle cx="50" cy="50" r="25" fill="none" stroke="#D4A537" strokeWidth="1" />
        <path d="M50 5 L50 95 M5 50 L95 50 M18 18 L82 82 M18 82 L82 18" stroke="#D4A537" strokeWidth="1" />
      </svg>
      <svg
        style={{
          position: 'absolute',
          bottom: -100,
          left: -100,
          width: 450,
          height: 450,
          opacity: 0.08,
          pointerEvents: 'none'
        }}
        viewBox="0 0 100 100"
      >
        <circle cx="50" cy="50" r="45" fill="none" stroke="#D4A537" strokeWidth="1.5" />
        <circle cx="50" cy="50" r="30" fill="none" stroke="#D4A537" strokeWidth="1" />
      </svg>

      {/* Outer Subtle Gold Border Frame */}
      <div
        style={{
          position: 'absolute',
          inset: 24,
          border: '2px solid rgba(212, 165, 55, 0.45)',
          borderRadius: 24,
          pointerEvents: 'none'
        }}
      />
      <div
        style={{
          position: 'absolute',
          inset: 32,
          border: '1px dashed rgba(212, 165, 55, 0.25)',
          borderRadius: 18,
          pointerEvents: 'none'
        }}
      />

      {/* TOP HEADER */}
      <div style={{ textAlign: 'center', marginTop: 12, position: 'relative', zIndex: 2 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 12 }}>
          <span style={{ color: '#D4A537', fontSize: 32 }}>🕉️</span>
          <h1
            style={{
              margin: 0,
              fontSize: isTelugu ? 48 : 42,
              fontWeight: 800,
              color: '#F28C28',
              letterSpacing: 1.5,
              textShadow: '0 2px 4px rgba(0,0,0,0.5)'
            }}
          >
            {isTelugu ? 'శుభోదయం' : 'Good Morning'}
          </h1>
          <span style={{ color: '#D4A537', fontSize: 32 }}>🕉️</span>
        </div>
        <p
          style={{
            margin: '8px 0 0 0',
            fontSize: 26,
            color: '#D4A537',
            fontWeight: 600
          }}
        >
          {isTelugu ? `${dateStrTe} • ${vaaramTe}` : `${dateStrEn} • ${vaaramEn}`}
        </p>
      </div>

      {/* FESTIVAL BANNER (IF ANY) */}
      {festival && (
        <div
          style={{
            backgroundColor: 'rgba(242, 140, 40, 0.22)',
            border: '2px solid #F28C28',
            borderRadius: 16,
            padding: '12px 24px',
            textAlign: 'center',
            margin: '8px 0',
            position: 'relative',
            zIndex: 2
          }}
        >
          <span style={{ color: '#D4A537', fontSize: 20, fontWeight: 700 }}>✨ విశేష పర్వదినం ✨</span>
          <div style={{ color: '#FFF7EC', fontSize: 30, fontWeight: 800, marginTop: 4 }}>
            {festival}
          </div>
        </div>
      )}

      {/* MIDDLE PANCHANG HIGHLIGHTS (LARGE & CLEAR) */}
      <div
        style={{
          backgroundColor: 'rgba(255, 247, 236, 0.05)',
          border: '1.5px solid rgba(212, 165, 55, 0.4)',
          borderRadius: 20,
          padding: '24px 32px',
          position: 'relative',
          zIndex: 2
        }}
      >
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: '1fr 1fr',
            gap: '20px 32px'
          }}
        >
          {/* Tithi */}
          <div style={{ borderBottom: '1px solid rgba(212, 165, 55, 0.2)', paddingBottom: 14 }}>
            <div style={{ fontSize: 20, color: '#D4A537', fontWeight: 600 }}>
              {isTelugu ? 'తిథి (Tithi)' : 'Tithi'}
            </div>
            <div style={{ fontSize: 34, color: '#FFF7EC', fontWeight: 800, marginTop: 4 }}>
              {isTelugu ? tithiTe : tithiEn}
            </div>
          </div>

          {/* Nakshatram */}
          <div style={{ borderBottom: '1px solid rgba(212, 165, 55, 0.2)', paddingBottom: 14 }}>
            <div style={{ fontSize: 20, color: '#D4A537', fontWeight: 600 }}>
              {isTelugu ? 'నక్షత్రం (Nakshatra)' : 'Nakshatra'}
            </div>
            <div style={{ fontSize: 34, color: '#FFF7EC', fontWeight: 800, marginTop: 4 }}>
              {isTelugu ? nakshatraTe : nakshatraEn}
            </div>
          </div>

          {/* Paksham */}
          <div>
            <div style={{ fontSize: 20, color: '#D4A537', fontWeight: 600 }}>
              {isTelugu ? 'పక్షం (Paksha)' : 'Paksha'}
            </div>
            <div style={{ fontSize: 30, color: '#FFF7EC', fontWeight: 700, marginTop: 4 }}>
              {isTelugu ? pakshaTe : pakshaEn}
            </div>
          </div>

          {/* Sun Times */}
          <div>
            <div style={{ fontSize: 20, color: '#D4A537', fontWeight: 600 }}>
              {isTelugu ? 'సూర్యోదయం / సూర్యాస్తమయం' : 'Sunrise / Sunset'}
            </div>
            <div style={{ fontSize: 28, color: '#FFF7EC', fontWeight: 700, marginTop: 4 }}>
              🌅 {sunrise} | 🌇 {sunset}
            </div>
          </div>
        </div>
      </div>

      {/* HIGHLIGHT BOXES: RAHU KALAM, YAMAGANDAM, ABHIJIT */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: '1fr 1fr 1fr',
          gap: 16,
          position: 'relative',
          zIndex: 2
        }}
      >
        {/* Rahu Kalam (Red Tint) */}
        <div
          style={{
            backgroundColor: 'rgba(211, 47, 47, 0.25)',
            border: '1.5px solid rgba(255, 107, 107, 0.7)',
            borderRadius: 16,
            padding: '16px 14px',
            textAlign: 'center'
          }}
        >
          <div style={{ fontSize: 18, color: '#FF8A80', fontWeight: 700 }}>
            {isTelugu ? 'రాహుకాలం' : 'Rahu Kalam'}
          </div>
          <div style={{ fontSize: 22, color: '#FFFFFF', fontWeight: 800, marginTop: 6 }}>
            {rahuKalam}
          </div>
        </div>

        {/* Yamagandam */}
        <div
          style={{
            backgroundColor: 'rgba(242, 140, 40, 0.2)',
            border: '1.5px solid rgba(242, 140, 40, 0.6)',
            borderRadius: 16,
            padding: '16px 14px',
            textAlign: 'center'
          }}
        >
          <div style={{ fontSize: 18, color: '#FFD180', fontWeight: 700 }}>
            {isTelugu ? 'యమగండం' : 'Yamagandam'}
          </div>
          <div style={{ fontSize: 22, color: '#FFFFFF', fontWeight: 800, marginTop: 6 }}>
            {yamagandam}
          </div>
        </div>

        {/* Abhijit Muhurtham (Green Tint) */}
        <div
          style={{
            backgroundColor: 'rgba(46, 125, 50, 0.28)',
            border: '1.5px solid rgba(129, 199, 132, 0.7)',
            borderRadius: 16,
            padding: '16px 14px',
            textAlign: 'center'
          }}
        >
          <div style={{ fontSize: 18, color: '#A5D6A7', fontWeight: 700 }}>
            {isTelugu ? 'అభిజిత్ ముహూర్తం' : 'Abhijit Muhurtham'}
          </div>
          <div style={{ fontSize: 22, color: '#FFFFFF', fontWeight: 800, marginTop: 6 }}>
            {abhijitMuhurtham}
          </div>
        </div>
      </div>

      {/* DAILY SHLOKA / QUOTE ROTATED BY DAY OF YEAR */}
      <div
        style={{
          backgroundColor: 'rgba(212, 165, 55, 0.1)',
          borderLeft: '4px solid #D4A537',
          borderRadius: '0 14px 14px 0',
          padding: '16px 20px',
          position: 'relative',
          zIndex: 2
        }}
      >
        <div style={{ fontSize: 16, color: '#D4A537', fontWeight: 700, letterSpacing: 1 }}>
          {isTelugu ? 'నేటి సుభాషితం (Daily Wisdom):' : 'Daily Shloka / Wisdom:'}
        </div>
        <div style={{ fontSize: 22, color: '#FFF7EC', fontWeight: 600, marginTop: 6, lineHeight: 1.4 }}>
          {isTelugu ? quote.telugu : quote.english}
        </div>
        {quote.source && (
          <div style={{ fontSize: 16, color: '#F28C28', textAlign: 'right', marginTop: 4, fontWeight: 500 }}>
            — {quote.source}
          </div>
        )}
      </div>

      {/* FOOTER BAR WITH APP BRANDING AND QR CODE */}
      <div
        style={{
          backgroundColor: '#3D0712',
          borderTop: '2px solid rgba(212, 165, 55, 0.4)',
          borderRadius: 16,
          padding: '16px 24px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          position: 'relative',
          zIndex: 2
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          {/* App Icon */}
          <div
            style={{
              width: 58,
              height: 58,
              borderRadius: 14,
              backgroundColor: '#5B0E1E',
              border: '2px solid #D4A537',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: 32
            }}
          >
            ☀️
          </div>
          <div>
            <div style={{ fontSize: 24, fontWeight: 800, color: '#D4A537' }}>
              శుభోదయ • Shubhodaya App
            </div>
            <div style={{ fontSize: 16, color: '#FFF7EC', opacity: 0.85 }}>
              Download free on Google Play Store
            </div>
          </div>
        </div>

        {/* QR Code representation pointing to PLAY_STORE_URL */}
        <div
          style={{
            backgroundColor: '#FFFFFF',
            padding: 6,
            borderRadius: 8,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center'
          }}
        >
          <img
            src={`https://api.qrserver.com/v1/create-qr-code/?size=64x64&data=${encodeURIComponent(PLAY_STORE_URL)}`}
            alt="Play Store QR"
            width="56"
            height="56"
            style={{ display: 'block' }}
          />
          <span style={{ color: '#000000', fontSize: 9, fontWeight: 700, marginTop: 2 }}>SCAN APP</span>
        </div>
      </div>
    </div>
  );
};
