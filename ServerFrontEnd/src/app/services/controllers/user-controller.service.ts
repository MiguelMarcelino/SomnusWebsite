import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable, Injector } from "@angular/core";
import { Observable } from "rxjs";
import { UserModel } from "src/app/models/user.model";
import { AuthenticationService } from "../authentication/authentication.service";
import { AppRoutesService } from "../routes/app-routes.service";
import { TemplateControllerService } from "./template-controller.service";

@Injectable({
    providedIn: 'root'
})
export class UserController extends TemplateControllerService<UserModel>{
  
    constructor( 
      protected http: HttpClient,
      private appRoutes: AppRoutesService
    ) {
      super(http);
    }

    protected getApiUrlAll() {
        throw new Error("Method not implemented.");
    }
    protected getApiUrlObject() {
        throw new Error("Method not implemented.");
    }
  
    public authenticateUser(token: string): Observable<any> {
      this.httpOptions = {
        headers: new HttpHeaders({ 
            "Content-Type": "application/json",
            "firebaseToken": token
        })
      };
      return this.http.get(this.appRoutes.apiUsersEndPointAuth, this.httpOptions);
    }

    public registerUser(token: string, user: UserModel): Observable<any> {
      this.httpOptions = {
        headers: new HttpHeaders({ 
            "Content-Type": "application/json",
            "firebaseToken": token
        })
      };
      return this.http.post(this.appRoutes.apiUsersEndPointRegister, user, this.httpOptions);
    }

}