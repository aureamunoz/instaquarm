import {LitElement, html, css} from 'lit';
import '@vaadin/icon';
import '@vaadin/button';
import '@vaadin/text-field';
import './qui-alert.js';
import {until} from 'lit/directives/until.js';

export class DemoInstaquarm extends LitElement {

    webAuthn = new WebAuthn({
        callbackPath: '/q/webauthn/callback',
        registerPath: '/q/webauthn/register',
        loginPath: '/q/webauthn/login'
    });

    static styles = css`
      h1 {
        font-family: Pacifico, fantasy;
        font-size: 60px;
        font-style: normal;
        font-variant: normal;
        font-weight: 700;
        line-height: 26.4px;
        color: var(--main-highlight-text-color);
      }

      @media all and (min-width:0px) and (max-width: 1024px) {
        .content {
          margin-left: 10px;
          margin-right: 10px;
        }
      }

    `

    static properties = {
        "_connected": {state: true},
        "_username": "<init>",
        "_notification": {state: true}
    }

    connectedCallback() {
        super.connectedCallback();
        fetch('/login/me')
            .then(response => response.text())
            .then(name => {
                if (name.includes("not logged in")) {
                    this._username = "";
                    this._connected = false;
                } else {
                    this._username = name;
                    this._connected = true;
                }
            });
    }

    render() {
        return html`${until(this._render(), html`<span>Loading...</span>`)}`;
    }

    _render() {
        if (this._username !== "<init>") {
            if (this._connected) {
                // Snapshot
                return this._renderSnapshotView();
            } else {
                return html`
                    <div class="content">
                    ${this._notification}
                    <h3>Login</h3>
                    <vaadin-text-field class="input-column" theme="small"
                                       placeholder="login" id="input-login" @keydown="${this._keydown}">
                        <vaadin-icon slot="suffix" icon="font-awesome-solid:right-to-bracket" class="login-button"
                                     id="login-button" @click="${this._login}"></vaadin-icon>
                    </vaadin-text-field>
                    <h3>Register</h3>
                    <vaadin-text-field class="input-column" theme="small"
                                       placeholder="username" id="username"></vaadin-text-field>
                    <vaadin-text-field class="input-column" theme="small"
                                       placeholder="first name" id="first"></vaadin-text-field>
                    <vaadin-text-field class="input-column" theme="small"
                                       placeholder="last name" id="last"></vaadin-text-field>
                    <vaadin-button @click="${this._register}" id="register">
                        Register
                        <vaadin-icon slot="suffix" icon="font-awesome-solid:right-to-bracket" class="login-button"
                                     id="login-button" @click="${this._login}"></vaadin-icon>
                    </vaadin-button>
                    </div>
                `
            }
        }
    }

    _renderSnapshotView() {
        return html`
            <div class="content">
                ${this._notification}
                <input id="snapshot" type="file" accept="image/*;capture=camera">
                <vaadin-text-field id="title" placeholder="title"></vaadin-text-field>
                <vaadin-button  @click="${this._snapshot}" id="click-photo">Upload!</vaadin-button>
            </div>
        `
    }

    _snapshot() {
        console.log("uploading")
        const image = this.shadowRoot.querySelector("#snapshot").files[0];
        image.arrayBuffer().then(r => {
            const bytes = this._arrayBufferToBase64(r);
            console.log(bytes);
            const title = this.shadowRoot.querySelector("#title").value;
            const req = {
                'user': this._username,
                'image': bytes,
                'title': title
            };
            console.log("Request", req);
            fetch('/pictures/new', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(req)
            })
                .then(response => {
                    console.log(JSON.stringify(response),"Response status",response.status);
                    if(response.ok) {
                        this._notification = html`        
                            <qui-alert level="success" dismissible showIcon>
                                <p>Picture uploaded!</p>
                            </qui-alert>`
                    } if (response.status===500){
                            this._notification = html`        
                                <qui-alert level="warning" dismissible showIcon>
                                    <p>Circuit Breaker is open!</p>
                                </qui-alert>`
                    }else {
                            console.log(JSON.stringify(response),"mensaje",response.body);
                            this._notification = html`        
                            <qui-alert level="error" dismissible showIcon>
                                <p>Picture uploading failed!</p>
                            </qui-alert>`
                    }
                })
        })
    }

    _arrayBufferToBase64( buffer ) {
        let binary = '';
        const bytes = new Uint8Array( buffer );
        const len = bytes.byteLength;
        for (let i = 0; i < len; i++) {
            binary += String.fromCharCode( bytes[ i ] );
        }
        return window.btoa( binary );
    }


    _login() {
        const userName = this.shadowRoot.querySelector("#input-login").value;
        console.log("login", userName);
        this.webAuthn.login({name: userName})
            .then(body => {
                this._notification = html`
                    <qui-alert level="success" dismissible showIcon>
                        <p>Login successful for ${userName}</p>
                    </qui-alert>`
                this._username = userName;
                this._connected = true;
            })
            .catch(err => {
                console.log(err);
                this._notification = html`
                    <qui-alert level="error" dismissible showIcon>
                        <p>Login failed.</p>
                    </qui-alert>`
            });
        return false;
    }

    _register() {
        const userName = this.shadowRoot.getElementById('username').value;
        const firstName = this.shadowRoot.getElementById('first').value;
        const lastName = this.shadowRoot.getElementById('last').value;
        console.log("registration", userName, firstName, lastName);
        this.webAuthn.register({name: userName, displayName: firstName + " " + lastName})
            .then(body => {
                this.shadowRoot.getElementById('username').value = "";
                this.shadowRoot.getElementById('first').value = "";
                this.shadowRoot.getElementById('last').value = "";
                this._username = userName;
                this._connected = true;
            })
            .catch(err => {
                this._username = "";
                this._notification = html`
                    <qui-alert level="error" dismissible showIcon>
                        <p>Registration failed.</p>
                    </qui-alert>`
                this._connected = false;
            });
        return false;
    }
}

customElements.define('demo-instaquarm', DemoInstaquarm);