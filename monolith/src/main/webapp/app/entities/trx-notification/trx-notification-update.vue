<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.trxNotification.home.createOrEditLabel"
          data-cy="TrxNotificationCreateUpdateHeading"
          v-text="t$('monolithApp.trxNotification.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="trxNotification.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="trxNotification.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxNotification.recipient')" for="trx-notification-recipient"></label>
            <input
              type="text"
              class="form-control"
              name="recipient"
              id="trx-notification-recipient"
              data-cy="recipient"
              :class="{ valid: !v$.recipient.$invalid, invalid: v$.recipient.$invalid }"
              v-model="v$.recipient.$model"
              required
            />
            <div v-if="v$.recipient.$anyDirty && v$.recipient.$invalid">
              <small class="form-text text-danger" v-for="error of v$.recipient.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.trxNotification.messageType')"
              for="trx-notification-messageType"
            ></label>
            <input
              type="text"
              class="form-control"
              name="messageType"
              id="trx-notification-messageType"
              data-cy="messageType"
              :class="{ valid: !v$.messageType.$invalid, invalid: v$.messageType.$invalid }"
              v-model="v$.messageType.$model"
              required
            />
            <div v-if="v$.messageType.$anyDirty && v$.messageType.$invalid">
              <small class="form-text text-danger" v-for="error of v$.messageType.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxNotification.content')" for="trx-notification-content"></label>
            <input
              type="text"
              class="form-control"
              name="content"
              id="trx-notification-content"
              data-cy="content"
              :class="{ valid: !v$.content.$invalid, invalid: v$.content.$invalid }"
              v-model="v$.content.$model"
              required
            />
            <div v-if="v$.content.$anyDirty && v$.content.$invalid">
              <small class="form-text text-danger" v-for="error of v$.content.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxNotification.sentAt')" for="trx-notification-sentAt"></label>
            <div class="d-flex">
              <input
                id="trx-notification-sentAt"
                data-cy="sentAt"
                type="datetime-local"
                class="form-control"
                name="sentAt"
                :class="{ valid: !v$.sentAt.$invalid, invalid: v$.sentAt.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.sentAt.$model)"
                @change="updateInstantField('sentAt', $event)"
              />
            </div>
            <div v-if="v$.sentAt.$anyDirty && v$.sentAt.$invalid">
              <small class="form-text text-danger" v-for="error of v$.sentAt.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxNotification.customer')" for="trx-notification-customer"></label>
            <select
              class="form-control"
              id="trx-notification-customer"
              data-cy="customer"
              name="customer"
              v-model="trxNotification.customer"
            >
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  trxNotification.customer && mstCustomerOption.id === trxNotification.customer.id
                    ? trxNotification.customer
                    : mstCustomerOption
                "
                v-for="mstCustomerOption in mstCustomers"
                :key="mstCustomerOption.id"
              >
                {{ mstCustomerOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./trx-notification-update.component.ts"></script>
