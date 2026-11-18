(() => {
  const I18N = {
    en: {
      engineChecking: 'Initializing engine...',
      engineReady: 'Engine Ready',
      tabSave: 'Save',
      tabLibrary: 'Library',
      tabAbout: 'Info',
      heroTitle: 'Personal Media Saver & Audio Extractor',
      heroSub: 'Paste any authorized public media link or direct video/audio URL to save offline or extract crisp MP3 audio directly on your phone.',
      capOnDevice: 'On-Device FFmpeg',
      capShare: 'Share Sheet Ready',
      inputPlaceholder: 'Paste a direct video, audio, or public media URL...',
      btnPaste: 'Paste',
      btnAnalyze: 'Analyze Link',
      btnAnalyzing: 'Analyzing...',
      chooseFormat: 'Choose output format & quality',
      saveBtn: 'Save',
      btnOpenFile: 'Open File',
      btnShareFile: 'Share',
      feat1Title: '100% On-Device',
      feat1Desc: 'Processes and converts media locally without external cloud servers.',
      feat2Title: 'Clean MP3 Audio',
      feat2Desc: 'Extracts high-clarity 192kbps MP3 tracks with embedded FFmpeg.',
      feat3Title: 'Private & Local',
      feat3Desc: 'No account required. Files save directly to Downloads/LuminaSaver.',
      policyFootnote: 'Only save media you own or have explicit permission to download. YouTube URLs are disabled to comply with Google Play policies.',
      libraryTitle: 'Saved Library',
      btnClearHistory: 'Clear List',
      libraryEmpty: 'No saved files yet. Saved videos and MP3 tracks will appear here.',
      aboutTitle: 'About Lumina Saver',
      aboutBody: 'Lumina Saver is a standalone on-device media saver and audio extractor from the Lumina utility suite. All downloads and FFmpeg MP3 conversions execute directly on your device and save straight into your public Downloads/LuminaSaver folder.',
      versionLabel: 'Version',
      saveLocationLabel: 'Save Folder',
      complianceTitle: 'Copyright & Policy Notice',
      complianceBody: 'This utility is intended strictly for personal backups, royalty-free media, and public links you are authorized to save. Downloading copyrighted content without permission or downloading from YouTube is strictly prohibited.',
      privacyTitle: 'Ad Privacy Preferences',
      privacyDesc: 'Manage your Google advertising consent choices at any time.',
      btnPrivacy: 'Manage Ad Consent',
      errEmptyUrl: 'Please enter or paste a valid media link first.',
      errEmptyClipboard: 'Clipboard is empty.',
      processingPrefix: 'Processing: ',
      startingDownload: 'Starting on-device save...',
      downloadDoneTitle: 'Saved successfully!',
      downloadDonePrefix: 'Saved in Downloads/LuminaSaver: ',
      downloadFailTitle: 'Save failed',
      openAction: 'Open',
      shareAction: 'Share'
    },
    ar: {
      engineChecking: 'جاري تهيئة المحرك...',
      engineReady: 'المحرك جاهز',
      tabSave: 'حفظ',
      tabLibrary: 'المكتبة',
      tabAbout: 'حول',
      heroTitle: 'حافظ الوسائط الشخصي ومستخرج الصوت',
      heroSub: 'الصق أي رابط وسائط عام مصرح به أو رابط فيديو/صوت مباشر لحفظه على هاتفك أو استخراج صوت MP3 نقي بالكامل على الجهاز.',
      capOnDevice: 'محرك FFmpeg مدمج',
      capShare: 'يدعم المشاركة المباشرة',
      inputPlaceholder: 'ضع رابط فيديو أو صوت مباشر أو رابط وسائط عام...',
      btnPaste: 'لصق',
      btnAnalyze: 'تحليل الرابط',
      btnAnalyzing: 'جاري التحليل...',
      chooseFormat: 'اختر صيغة وجودة الحفظ',
      saveBtn: 'حفظ',
      btnOpenFile: 'فتح الملف',
      btnShareFile: 'مشاركة',
      feat1Title: 'معالجة كاملة على الجهاز',
      feat1Desc: 'تحويل وحفظ محلي بالكامل بدون أي خوادم خارجية.',
      feat2Title: 'صوت MP3 عالي النقاء',
      feat2Desc: 'استخراج صوت MP3 بجودة 192kbps باستخدام محرك FFmpeg المدمج.',
      feat3Title: 'خصوصية وأمان محلي',
      feat3Desc: 'بدون حساب. تُحفظ الملفات مباشرة في مجلد Downloads/LuminaSaver.',
      policyFootnote: 'احفظ فقط الوسائط التي تملكها أو لديك إذن صريح بتنزيلها. روابط يوتيوب معطّلة التزاماً بسياسات Google Play.',
      libraryTitle: 'الملفات المحفوظة',
      btnClearHistory: 'مسح القائمة',
      libraryEmpty: 'لا توجد ملفات محفوظة بعد. ستظهر الفيديوهات والملفات الصوتية هنا.',
      aboutTitle: 'حول Lumina Saver',
      aboutBody: 'Lumina Saver أداة مستقلة لحفظ الوسائط واستخراج الصوت على الجهاز ضمن مجموعة تطبيقات Lumina. تتم جميع عمليات التحويل عبر FFmpeg محلياً وتُحفظ مباشرة في مجلد Downloads/LuminaSaver.',
      versionLabel: 'الإصدار',
      saveLocationLabel: 'مجلد الحفظ',
      complianceTitle: 'إشعار حقوق الملكية والسياسات',
      complianceBody: 'هذا التطبيق مخصص حصراً للنسخ الاحتياطي الشخصي والوسائط المجانية والروابط العامة المصرح بحفظها. يُحظر تماماً تنزيل المحتوى المحمي بدون إذن أو التنزيل من يوتيوب.',
      privacyTitle: 'إعدادات خصوصية الإعلانات',
      privacyDesc: 'يمكنك تعديل تفضيلات موافقة إعلانات Google في أي وقت.',
      btnPrivacy: 'إدارة موافقة الإعلانات',
      errEmptyUrl: 'يرجى إدخال أو لصق رابط وسائط صالح أولاً.',
      errEmptyClipboard: 'الحافظة فارغة.',
      processingPrefix: 'جاري المعالجة: ',
      startingDownload: 'جاري بدء الحفظ على الجهاز...',
      downloadDoneTitle: 'تم الحفظ بنجاح!',
      downloadDonePrefix: 'تم الحفظ في Downloads/LuminaSaver: ',
      downloadFailTitle: 'فشل الحفظ',
      openAction: 'فتح',
      shareAction: 'مشاركة'
    }
  };

  let currentLang = localStorage.getItem('lumina_lang') || 'en';
  let currentTheme = localStorage.getItem('lumina_theme') || 'dark';
  let currentMediaUrl = '';
  let currentMediaTitle = '';
  let lastSavedUri = '';
  let lastSavedMime = '';

  const t = (key) => (I18N[currentLang] && I18N[currentLang][key]) || I18N.en[key] || key;

  document.addEventListener('DOMContentLoaded', () => {
    const html = document.documentElement;
    const urlInput = document.getElementById('urlInput');
    const btnClear = document.getElementById('btnClear');
    const btnPaste = document.getElementById('btnPaste');
    const btnAnalyze = document.getElementById('btnAnalyze');
    const btnSpinner = document.getElementById('btnSpinner');
    const btnAnalyzeText = document.getElementById('btnAnalyzeText');
    const btnLang = document.getElementById('btnLang');
    const langLabel = document.getElementById('langLabel');
    const btnTheme = document.getElementById('btnTheme');
    const themeIconSun = document.getElementById('themeIconSun');
    const themeIconMoon = document.getElementById('themeIconMoon');

    const alertBox = document.getElementById('alertBox');
    const alertMsg = document.getElementById('alertMsg');
    const resultCard = document.getElementById('resultCard');
    const videoThumb = document.getElementById('videoThumb');
    const thumbPlaceholder = document.getElementById('thumbPlaceholder');
    const videoTitle = document.getElementById('videoTitle');
    const videoDuration = document.getElementById('videoDuration');
    const videoAuthor = document.getElementById('videoAuthor');
    const videoPlatform = document.getElementById('videoPlatform');
    const optionsGrid = document.getElementById('optionsGrid');

    const progressPanel = document.getElementById('progressPanel');
    const jobTitle = document.getElementById('jobTitle');
    const progressPct = document.getElementById('progressPct');
    const progressBarFill = document.getElementById('progressBarFill');
    const downloadStatusText = document.getElementById('downloadStatusText');
    const completeActions = document.getElementById('completeActions');
    const btnOpenSaved = document.getElementById('btnOpenSaved');
    const btnShareSaved = document.getElementById('btnShareSaved');

    const enginePill = document.getElementById('engineStatusPill');
    const engineStatusText = document.getElementById('engineStatusText');
    const libraryList = document.getElementById('libraryList');
    const libraryEmpty = document.getElementById('libraryEmpty');
    const libraryCount = document.getElementById('libraryCount');
    const btnClearHistory = document.getElementById('btnClearHistory');
    const appVersionText = document.getElementById('appVersionText');
    const privacyCard = document.getElementById('privacyCard');
    const btnPrivacySettings = document.getElementById('btnPrivacySettings');

    function applyTheme(theme) {
      currentTheme = theme;
      localStorage.setItem('lumina_theme', theme);
      html.setAttribute('data-theme', theme);
      const isDark = theme === 'dark';
      themeIconSun.style.display = isDark ? 'block' : 'none';
      themeIconMoon.style.display = isDark ? 'none' : 'block';
      if (window.LuminaNative && window.LuminaNative.setSystemTheme) {
        window.LuminaNative.setSystemTheme(isDark);
      }
    }

    function applyLanguage(lang) {
      currentLang = lang;
      localStorage.setItem('lumina_lang', lang);
      html.lang = lang;
      html.dir = lang === 'ar' ? 'rtl' : 'ltr';
      langLabel.textContent = lang === 'ar' ? 'EN' : 'عربي';

      document.querySelectorAll('[data-i18n]').forEach((el) => {
        const key = el.getAttribute('data-i18n');
        el.textContent = t(key);
      });
      document.querySelectorAll('[data-i18n-placeholder]').forEach((el) => {
        const key = el.getAttribute('data-i18n-placeholder');
        el.placeholder = t(key);
      });
      updateEngineStatusUI();
      renderLibrary();
    }

    function updateEngineStatusUI() {
      const ready = !(window.LuminaNative && window.LuminaNative.isEngineReady) || window.LuminaNative.isEngineReady();
      if (ready) {
        enginePill.classList.add('ready');
        engineStatusText.textContent = t('engineReady');
      } else {
        enginePill.classList.remove('ready');
        engineStatusText.textContent = t('engineChecking');
      }
    }

    // Poll engine readiness briefly on first launch until extracted
    const engineTimer = setInterval(() => {
      updateEngineStatusUI();
      if (enginePill.classList.contains('ready')) {
        clearInterval(engineTimer);
      }
    }, 800);

    // Tab switching
    document.querySelectorAll('.nav-tab').forEach((tabBtn) => {
      tabBtn.addEventListener('click', () => {
        const target = tabBtn.getAttribute('data-tab');
        document.querySelectorAll('.nav-tab').forEach((b) => {
          b.classList.remove('active');
          b.setAttribute('aria-selected', 'false');
        });
        document.querySelectorAll('.tab-panel').forEach((p) => p.classList.remove('active'));
        tabBtn.classList.add('active');
        tabBtn.setAttribute('aria-selected', 'true');
        document.getElementById('panel-' + target).classList.add('active');
        if (target === 'library') renderLibrary();
      });
    });

    btnTheme.addEventListener('click', () => applyTheme(currentTheme === 'dark' ? 'light' : 'dark'));
    btnLang.addEventListener('click', () => applyLanguage(currentLang === 'en' ? 'ar' : 'en'));

    urlInput.addEventListener('input', () => {
      btnClear.style.display = urlInput.value.length > 0 ? 'inline-block' : 'none';
    });

    btnClear.addEventListener('click', () => {
      urlInput.value = '';
      btnClear.style.display = 'none';
      hideAlert();
      urlInput.focus();
    });

    function hideAlert() {
      alertBox.style.display = 'none';
    }

    function setLoading(loading) {
      btnSpinner.style.display = loading ? 'inline-block' : 'none';
      btnAnalyzeText.textContent = loading ? t('btnAnalyzing') : t('btnAnalyze');
      btnAnalyze.disabled = loading;
      if (loading) hideAlert();
    }

    window.showAlert = function (msg) {
      alertMsg.textContent = msg;
      alertBox.style.display = 'flex';
      setLoading(false);
    };

    // Native Android clipboard paste
    btnPaste.addEventListener('click', async () => {
      hideAlert();
      let clip = '';
      if (window.LuminaNative && window.LuminaNative.getClipboardText) {
        clip = window.LuminaNative.getClipboardText() || '';
      } else if (navigator.clipboard && navigator.clipboard.readText) {
        try { clip = (await navigator.clipboard.readText()) || ''; } catch (_) {}
      }
      if (clip.trim()) {
        urlInput.value = clip.trim();
        btnClear.style.display = 'inline-block';
        triggerAnalyze();
      } else {
        window.showAlert(t('errEmptyClipboard'));
      }
    });

    urlInput.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') triggerAnalyze();
    });

    btnAnalyze.addEventListener('click', triggerAnalyze);

    function triggerAnalyze() {
      const url = urlInput.value.trim();
      if (!url) {
        window.showAlert(t('errEmptyUrl'));
        return;
      }
      setLoading(true);
      resultCard.style.display = 'none';
      progressPanel.style.display = 'none';

      if (window.LuminaNative && window.LuminaNative.inspectUrl) {
        window.LuminaNative.inspectUrl(url, currentLang);
      } else {
        setLoading(false);
        window.showAlert('Native Android engine required.');
      }
    }

    window.renderVideoData = function (data) {
      setLoading(false);
      currentMediaUrl = data.url || urlInput.value.trim();
      currentMediaTitle = data.title || 'Lumina_Media';

      videoTitle.textContent = currentMediaTitle;
      videoDuration.textContent = data.duration || '--:--';
      videoAuthor.textContent = data.uploader || 'Web';
      videoPlatform.textContent = data.platform || 'Media';

      if (data.thumbnail) {
        videoThumb.src = data.thumbnail;
        videoThumb.style.display = 'block';
        thumbPlaceholder.style.display = 'none';
        videoThumb.onerror = () => {
          videoThumb.style.display = 'none';
          thumbPlaceholder.style.display = 'flex';
        };
      } else {
        videoThumb.style.display = 'none';
        thumbPlaceholder.style.display = 'flex';
      }

      optionsGrid.innerHTML = '';
      (data.options || []).forEach((opt) => {
        const isAudio = opt.type === 'audio' || opt.ext === 'mp3';
        const row = document.createElement('div');
        row.className = 'option-item';
        row.innerHTML = `
          <div class="option-left">
            <div class="opt-type-icon">
              ${isAudio
                ? '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 18V5l12-2v13"/><circle cx="6" cy="18" r="3"/><circle cx="18" cy="16" r="3"/></svg>'
                : '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="4" width="20" height="16" rx="2"/><polygon points="10 9 15 12 10 15 10 9"/></svg>'}
            </div>
            <div>
              <div class="opt-label"></div>
              <div class="opt-sub"></div>
            </div>
          </div>
          <button class="opt-btn" type="button"></button>
        `;
        row.querySelector('.opt-label').textContent = opt.label;
        row.querySelector('.opt-sub').textContent = `${opt.badge} • ${(opt.ext || 'mp4').toUpperCase()}`;
        row.querySelector('.opt-btn').textContent = t('saveBtn');

        row.addEventListener('click', () => startDownload(opt));
        optionsGrid.appendChild(row);
      });

      resultCard.style.display = 'block';
      resultCard.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    };

    function startDownload(opt) {
      hideAlert();
      progressPanel.style.display = 'block';
      completeActions.style.display = 'none';
      progressBarFill.classList.remove('error');
      progressBarFill.style.width = '8%';
      progressPct.textContent = '8%';
      jobTitle.textContent = t('processingPrefix') + opt.label;
      downloadStatusText.textContent = t('startingDownload');
      progressPanel.scrollIntoView({ behavior: 'smooth', block: 'nearest' });

      if (window.LuminaNative && window.LuminaNative.startDownload) {
        window.LuminaNative.startDownload(currentMediaUrl, opt.type, opt.quality, currentMediaTitle, currentLang);
      }
    }

    window.updateNativeProgress = function (percent, statusText) {
      progressBarFill.classList.remove('error');
      progressBarFill.style.width = `${percent}%`;
      progressPct.textContent = `${percent}%`;
      downloadStatusText.textContent = statusText;
    };

    window.onNativeDownloadReady = function (filename, uriString, mimeType) {
      lastSavedUri = uriString || '';
      lastSavedMime = mimeType || '';
      progressBarFill.classList.remove('error');
      progressBarFill.style.width = '100%';
      progressPct.textContent = '100%';
      jobTitle.textContent = t('downloadDoneTitle');
      downloadStatusText.textContent = t('downloadDonePrefix') + filename;
      completeActions.style.display = 'flex';
      renderLibrary();
    };

    window.onNativeDownloadFailed = function (errorMsg) {
      progressBarFill.classList.add('error');
      jobTitle.textContent = t('downloadFailTitle');
      downloadStatusText.textContent = errorMsg;
      completeActions.style.display = 'none';
      window.showAlert(errorMsg);
    };

    btnOpenSaved.addEventListener('click', () => {
      if (window.LuminaNative && window.LuminaNative.openSavedMedia) {
        window.LuminaNative.openSavedMedia(lastSavedUri, lastSavedMime);
      }
    });

    btnShareSaved.addEventListener('click', () => {
      if (window.LuminaNative && window.LuminaNative.shareSavedMedia) {
        window.LuminaNative.shareSavedMedia(lastSavedUri, lastSavedMime);
      }
    });

    function renderLibrary() {
      let items = [];
      try {
        if (window.LuminaNative && window.LuminaNative.getDownloadHistory) {
          items = JSON.parse(window.LuminaNative.getDownloadHistory() || '[]');
        }
      } catch (_) {
        items = [];
      }

      libraryCount.textContent = String(items.length);
      libraryCount.style.display = items.length > 0 ? 'inline-block' : 'none';
      libraryList.innerHTML = '';

      if (!items.length) {
        libraryEmpty.style.display = 'block';
        return;
      }
      libraryEmpty.style.display = 'none';

      items.forEach((item) => {
        const card = document.createElement('div');
        card.className = 'lib-item';
        card.innerHTML = `
          <div class="lib-info">
            <div class="lib-title"></div>
            <div class="lib-meta">
              <span class="lib-fmt"></span>
              <span class="lib-size"></span>
            </div>
          </div>
          <div class="lib-actions">
            <button class="lib-btn primary" type="button"></button>
            <button class="lib-btn" type="button"></button>
          </div>
        `;
        card.querySelector('.lib-title').textContent = item.title || item.filename;
        card.querySelector('.lib-fmt').textContent = item.format || 'MEDIA';
        card.querySelector('.lib-size').textContent = item.size || '';
        const [openBtn, shareBtn] = card.querySelectorAll('.lib-btn');
        openBtn.textContent = t('openAction');
        shareBtn.textContent = t('shareAction');

        openBtn.addEventListener('click', () => {
          window.LuminaNative && window.LuminaNative.openSavedMedia(item.uri, item.mimeType);
        });
        shareBtn.addEventListener('click', () => {
          window.LuminaNative && window.LuminaNative.shareSavedMedia(item.uri, item.mimeType);
        });
        libraryList.appendChild(card);
      });
    }

    btnClearHistory.addEventListener('click', () => {
      if (window.LuminaNative && window.LuminaNative.clearDownloadHistory) {
        window.LuminaNative.clearDownloadHistory();
      }
      renderLibrary();
    });

    // Version & UMP Ad Privacy Settings
    if (window.LuminaNative && window.LuminaNative.getAppVersion) {
      appVersionText.textContent = window.LuminaNative.getAppVersion();
    }
    setTimeout(() => {
      if (window.LuminaNative && window.LuminaNative.isPrivacyOptionsRequired && window.LuminaNative.isPrivacyOptionsRequired()) {
        privacyCard.style.display = 'block';
      }
    }, 2500);
    btnPrivacySettings.addEventListener('click', () => {
      window.LuminaNative && window.LuminaNative.openPrivacySettings();
    });

    // External Share Intent Hook
    window.handleExternalUrl = function (sharedUrl) {
      if (sharedUrl) {
        urlInput.value = sharedUrl;
        btnClear.style.display = 'inline-block';
        triggerAnalyze();
      }
    };

    applyTheme(currentTheme);
    applyLanguage(currentLang);
    updateEngineStatusUI();

    const params = new URLSearchParams(window.location.search);
    const initialShared = params.get('shared_url');
    if (initialShared) {
      window.handleExternalUrl(initialShared);
    }
  });
})();
