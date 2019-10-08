import { PolymerElement } from '@polymer/polymer/polymer-element.js';
import { html } from '@polymer/polymer/lib/utils/html-tag.js';
import '@polymer/iron-ajax/iron-ajax.js';
class OfflineBanner extends PolymerElement {
  static get template() {
    return html`
    <style>
      .offline[hidden] {
        display: none !important;
      }
    </style>

    <iron-ajax auto="" url="./offline-page.html" handle-as="document" last-response="{{offlinePage}}"></iron-ajax>

    <div id="offline" class="offline" hidden\$="[[online]]">
    </div>
`;
  }

  static get is() {
    return 'offline-banner';
  }

  static get observers() {
    return ['_offlinePageChanged(offlinePage)'];
  }

  // Reusing offline-page.html content in order not to duplicate.
  // The page is requested using iron-ajax.
  _offlinePageChanged(doc) {
    if (doc) {
      this.$.offline.appendChild(doc.querySelector('style'));
      this.$.offline.appendChild(doc.querySelector('.content'));
    }
  }

  ready() {
    super.ready();

    // This might be provided by flow in the future (#3778)
    this.online = window.navigator.onLine;
    window.addEventListener('online', e => this.online = true);
    window.addEventListener('offline', e => this.online = false);
  }
}

window.customElements.define(OfflineBanner.is, OfflineBanner);
