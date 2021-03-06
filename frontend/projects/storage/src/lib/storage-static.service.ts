import {Injectable} from '@angular/core';
import {WindowService} from 'projects/tools/src/lib/window.service';
import {CookieService} from 'ngx-cookie-service';
import {SecurityService} from 'projects/security/src/lib/security.service';
import {map} from 'rxjs/operators';
import {StorageConfigurationService} from 'projects/storage/src/lib/storage-configuration.service';
import {ConfigurationService} from 'projects/commons/src/lib/config/configuration.service';

@Injectable()
export class StorageStaticService {

  constructor(private window: WindowService,
              private cookies: CookieService,
              private security: SecurityService,
              private storageConfiguration: StorageConfigurationService) {
  }

  public openStaticPage(path: string) {
    this.window.open(this.security.token.pipe(
      map(token => {
        this.cookies.delete('JWT');
        // TODO try with secured cookie
        this.cookies.set('JWT', token, null, '/', null, false, 'Lax');
        return this.storageConfiguration.storageApiUrl(`/static/${path}`);
      })
    ));
  }
}
